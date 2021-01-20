package com.bytes.messenger.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bytes.messenger.LastSeen
import com.bytes.messenger.R
import com.bytes.messenger.databinding.ActivityImageProfileBinding
import com.bytes.messenger.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ImageProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getImage()
        clickListeners()
    }

    private fun getImage() {
        FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users: User? = snapshot.getValue(User::class.java)
                    if (users != null) {
                        val imageUrl = users.profileImage
                        if (imageUrl.isNotEmpty())
                            Glide.with(applicationContext).load(imageUrl).into(binding.profileImage)
                        else Glide.with(applicationContext).load(R.drawable.image_default_user)
                            .into(binding.profileImage)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun clickListeners() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.shareButton.setOnClickListener {
            Snackbar.make(findViewById(android.R.id.content),
                "Share Clicked",
                Snackbar.LENGTH_SHORT).show()
        }

        binding.editButton.setOnClickListener {
            Snackbar.make(findViewById(android.R.id.content),
                "Edit Clicked",
                Snackbar.LENGTH_SHORT).show()
        }
    }
}