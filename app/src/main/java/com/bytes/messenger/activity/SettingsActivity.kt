package com.bytes.messenger.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bytes.messenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SettingsActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        firestore = FirebaseFirestore.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        getUserInfo()
        clickListeners()
    }

    private fun clickListeners() {
        profile_container.setOnClickListener {
            startActivity(Intent(this@SettingsActivity, ProfileActivity::class.java))
        }
    }

    private fun getUserInfo() {
        GlobalScope.launch(Dispatchers.IO) {
            val reference = firestore.collection("Users").document(firebaseUser.uid).get().await()
            withContext(Dispatchers.Main) {
                name.text = reference.get("userName").toString()
                bio.text = reference.get("bio").toString()

                val imageUrl = reference.get("profileImage").toString()
                if (imageUrl.isNotEmpty())
                    Glide.with(applicationContext).load(imageUrl).into(image)
            }
        }

    }
}