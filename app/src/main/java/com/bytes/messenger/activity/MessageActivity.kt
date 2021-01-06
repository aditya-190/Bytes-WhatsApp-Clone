package com.bytes.messenger.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.bytes.messenger.MainActivity
import com.bytes.messenger.R
import kotlinx.android.synthetic.main.activity_message.*

class MessageActivity : AppCompatActivity() {
    private lateinit var receiverID: String
    private lateinit var lastSeen: String
    private lateinit var receiverName: String
    private lateinit var userImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        initialise()
        clickListeners()
    }

    private fun initialise() {
        receiverName = intent.getStringExtra("userName").toString()
        lastSeen = intent.getStringExtra("userLastSeen").toString()
        userImage = intent.getStringExtra("userImage").toString()
        receiverID = intent.getStringExtra("userID").toString()

        name.text = receiverName
        last_seen.text = lastSeen

        if (userImage.isNotEmpty())
            Glide.with(applicationContext).load(userImage).into(image)
    }

    private fun clickListeners() {

        message.addTextChangedListener {
            if (message.text.toString().trim().isNotEmpty()) {
                send_button.setImageResource(R.drawable.icon_send)
                photos.visibility = View.GONE
                attachment.visibility = View.GONE
            } else {
                send_button.setImageResource(R.drawable.icon_mic)
                photos.visibility = View.VISIBLE
                attachment.visibility = View.VISIBLE
            }
        }

        send_button.setOnClickListener {
            if (message.text.toString().trim().isNotEmpty()) {
                sendMessage(message.text.toString())
            }
        }

        back_button.setOnClickListener {
            startActivity(Intent(this@MessageActivity, MainActivity::class.java))
            finish()
        }
    }

    private fun sendMessage(message: String) {

    }

    override fun onBackPressed() {
        startActivity(Intent(this@MessageActivity, MainActivity::class.java))
        finish()
    }
}