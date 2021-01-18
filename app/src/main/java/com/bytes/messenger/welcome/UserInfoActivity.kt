package com.bytes.messenger.welcome

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bytes.messenger.MainActivity
import com.bytes.messenger.databinding.ActivityUserInfoBinding
import com.bytes.messenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserInfoBinding
    private val currentUser = FirebaseAuth.getInstance().currentUser!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nextButton.setOnClickListener {
            if (binding.name.text.trim().toString().isNotEmpty()) {
                binding.progressBar.visibility = View.VISIBLE

                val newUser = User(
                    currentUser.uid,
                    binding.name.text.trim().toString(),
                    currentUser.phoneNumber.toString(),
                    "",
                    "Hey there! I am using Bytes.",
                    "",
                    "")

                FirebaseDatabase.getInstance().reference.child("Users")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .setValue(newUser).addOnSuccessListener {
                        binding.progressBar.visibility = View.INVISIBLE
                        startActivity(Intent(this@UserInfoActivity, MainActivity::class.java))
                        finish()
                    }
            } else {
                binding.name.error = "Please type a name"
            }
        }
    }
}