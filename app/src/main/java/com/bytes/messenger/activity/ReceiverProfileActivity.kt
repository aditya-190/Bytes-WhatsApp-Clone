package com.bytes.messenger.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bytes.messenger.R
import com.bytes.messenger.databinding.ActivityReceiverProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ReceiverProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiverProfileBinding
    private lateinit var currentUser: FirebaseUser
    private lateinit var receiverID: String
    private lateinit var lastSeen: String
    private lateinit var receiverName: String
    private lateinit var receiverImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiverProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialise()
    }

    private fun initialise() {
        currentUser = FirebaseAuth.getInstance().currentUser!!
        receiverName = intent.getStringExtra("userName").toString()
        lastSeen = intent.getStringExtra("userLastSeen").toString()
        receiverImage = intent.getStringExtra("userImage").toString()
        receiverID = intent.getStringExtra("userID").toString()

        binding.toolbar.title = receiverName

        if (receiverImage.isNotEmpty())
            Glide.with(applicationContext).load(receiverImage).into(binding.image)
        else
            Glide.with(applicationContext).load(R.drawable.image_default_user).into(binding.image)

    }

    override fun onBackPressed() {
        startActivity(Intent(this@ReceiverProfileActivity, MessageActivity::class.java).also {
            it.putExtra("userID", receiverID)
            it.putExtra("userName", receiverName)
            it.putExtra("userImage", receiverImage)
            it.putExtra("userLastSeen", lastSeen)
        })
        finish()
    }
}