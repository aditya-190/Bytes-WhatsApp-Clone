package com.bytes.messenger.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bytes.messenger.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SettingsActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firestore: FirebaseFirestore

    private lateinit var userName: TextView
    private lateinit var userBio: TextView
    private lateinit var userImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initialise()
        getUserInfo()
        clickListeners()
    }

    private fun initialise() {
        userName = findViewById(R.id.user_name)
        userBio = findViewById(R.id.user_bio)
        userImage = findViewById(R.id.user_image)

        firestore = FirebaseFirestore.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
    }

    private fun clickListeners() {
        findViewById<LinearLayout>(R.id.profile_container).setOnClickListener {
            startActivity(Intent(this@SettingsActivity, ProfileActivity::class.java))
        }
    }

    private fun getUserInfo() {
        firestore.collection("Users").document(firebaseUser.uid).get().addOnSuccessListener {
            userName.text = it.get("userName").toString()
            userBio.text = it.get("bio").toString()

            val imageUrl = it.get("profileImage").toString()

            if (imageUrl.isNotEmpty())
                Glide.with(applicationContext).load(imageUrl).into(userImage)

        }.addOnFailureListener {
            Snackbar.make(findViewById(android.R.id.content),
                "Something went wrong.",
                Snackbar.LENGTH_SHORT).show()
        }
    }
}