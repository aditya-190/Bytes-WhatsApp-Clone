package com.bytes.messenger.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bytes.messenger.MainActivity
import com.bytes.messenger.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        getUserInfo()
        clickListeners()
    }

    private fun clickListeners() {
        binding.profileContainer.setOnClickListener {
            startActivity(Intent(this@SettingsActivity, ProfileActivity::class.java))
            finish()
        }
    }

    private fun getUserInfo() {
        GlobalScope.launch(Dispatchers.IO) {
            val reference = firestore.collection("Users").document(firebaseUser.uid).get().await()
            withContext(Dispatchers.Main) {
                binding.name.text = reference.get("userName").toString()
                binding.bio.text = reference.get("bio").toString()

                val imageUrl = reference.get("profileImage").toString()
                if (imageUrl.isNotEmpty())
                    Glide.with(applicationContext).load(imageUrl).into(binding.image)
            }
        }

    }
}