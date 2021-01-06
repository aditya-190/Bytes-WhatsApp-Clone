package com.bytes.messenger.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.bytes.messenger.MainActivity
import com.bytes.messenger.R
import com.bytes.messenger.databinding.ActivityMessageBinding

class MessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessageBinding
    private lateinit var receiverID: String
    private lateinit var lastSeen: String
    private lateinit var receiverName: String
    private lateinit var userImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialise()
        clickListeners()
    }

    private fun initialise() {
        receiverName = intent.getStringExtra("userName").toString()
        lastSeen = intent.getStringExtra("userLastSeen").toString()
        userImage = intent.getStringExtra("userImage").toString()
        receiverID = intent.getStringExtra("userID").toString()

        binding.name.text = receiverName
        binding.lastSeen.text = lastSeen

        if (userImage.isNotEmpty())
            Glide.with(applicationContext).load(userImage).into(binding.image)
    }

    private fun clickListeners() {

        binding.message.addTextChangedListener {
            if (binding.message.text.toString().trim().isNotEmpty()) {
                binding.sendButton.setImageResource(R.drawable.icon_send)
                binding.photos.visibility = View.GONE
                binding.attachment.visibility = View.GONE
            } else {
                binding.sendButton.setImageResource(R.drawable.icon_mic)
                binding.photos.visibility = View.VISIBLE
                binding.attachment.visibility = View.VISIBLE
            }
        }

        binding.sendButton.setOnClickListener {
            if (binding.message.text.toString().trim().isNotEmpty()) {
                sendMessage(binding.message.text.toString())
            }
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun sendMessage(message: String) {

    }
}