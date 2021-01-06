package com.bytes.messenger.welcome

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bytes.messenger.MainActivity
import com.bytes.messenger.R
import com.bytes.messenger.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pl.droidsonroids.gif.GifImageView

class UserInfoActivity : AppCompatActivity() {
    private lateinit var progressBar: GifImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        initialise()
        clickListeners()
    }

    private fun initialise() {
        progressBar = findViewById(R.id.progressBar)
    }

    private fun clickListeners() {
        findViewById<Button>(R.id.next_button).setOnClickListener {
            val username: String = findViewById<EditText>(R.id.user_name).text.trim().toString()
            if (username.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    val newUser = User(user.uid,
                        username,
                        user.phoneNumber!!,
                        "",
                        "Hey there! I am using Bytes.",
                        "",
                        "")
                    FirebaseFirestore.getInstance().collection("Users").document(user.uid)
                        .set(newUser)
                        .addOnSuccessListener {
                            progressBar.visibility = View.INVISIBLE
                            startActivity(Intent(this@UserInfoActivity, MainActivity::class.java))
                            finish()
                        }
                }
            } else {
                Snackbar.make(findViewById(android.R.id.content),
                    "Please enter your name.",
                    Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
        findViewById<CardView>(R.id.profile_image_container).setOnClickListener {
            Snackbar.make(findViewById(android.R.id.content), "Image", Snackbar.LENGTH_SHORT)
                .show()
        }
    }
}