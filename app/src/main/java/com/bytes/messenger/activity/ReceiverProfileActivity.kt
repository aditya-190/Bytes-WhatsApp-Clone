package com.bytes.messenger.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        lastSeen = intent.getStringExtra("lastSeen").toString()
        receiverImage = intent.getStringExtra("userImage").toString()
        receiverID = intent.getStringExtra("userID").toString()

    }

    override fun onBackPressed() {
        startActivity(Intent(this@ReceiverProfileActivity, MessageActivity::class.java))
        finish()
    }
}