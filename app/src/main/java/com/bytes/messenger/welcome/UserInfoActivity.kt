package com.bytes.messenger.welcome

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bytes.messenger.FirebaseServices
import com.bytes.messenger.MainActivity
import com.bytes.messenger.databinding.ActivityUserInfoBinding
import com.bytes.messenger.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nextButton.setOnClickListener {
            if (binding.name.text.trim().toString().isNotEmpty()) {
                binding.progressBar.visibility = View.VISIBLE

                val newUser = User(
                    FirebaseAuth.getInstance().currentUser!!.uid,
                    binding.name.text.trim().toString(),
                    FirebaseAuth.getInstance().currentUser!!.phoneNumber.toString(),
                    "",
                    "Hey there! I am using Bytes.",
                    "",
                    "")

                GlobalScope.launch(Dispatchers.IO) {
                    FirebaseServices.addUser(newUser)

                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.INVISIBLE
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
    }
}