package com.bytes.messenger.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bytes.messenger.databinding.ActivitySettingsBinding
import com.bytes.messenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users: User? = snapshot.getValue(User::class.java)
                    if (users != null) {
                        binding.name.text = users.userName
                        binding.bio.text = users.bio
                        val imageUrl = users.profileImage
                        if (imageUrl.isNotEmpty())
                            Glide.with(applicationContext).load(imageUrl).into(binding.image)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}