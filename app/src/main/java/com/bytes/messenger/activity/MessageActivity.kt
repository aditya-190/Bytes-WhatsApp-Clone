package com.bytes.messenger.activity

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.bytes.messenger.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MessageActivity : AppCompatActivity() {

    private lateinit var sendMessage: FloatingActionButton
    private lateinit var message: EditText
    private lateinit var receiverID: String
    private lateinit var lastSeen: String
    private lateinit var receiverName: String
    private lateinit var userImage: String
    private lateinit var attachments: ImageView
    private lateinit var photoPicker: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        initialise()
        clickListeners()
    }

    private fun initialise() {
        sendMessage = findViewById(R.id.voice_message_n_send_button)
        message = findViewById(R.id.message)
        attachments = findViewById(R.id.select_attachment)
        photoPicker = findViewById(R.id.select_photo)

        receiverName = intent.getStringExtra("userName").toString()
        lastSeen = intent.getStringExtra("userLastSeen").toString()
        userImage = intent.getStringExtra("userImage").toString()
        receiverID = intent.getStringExtra("userID").toString()

        findViewById<TextView>(R.id.user_name).text = receiverName
        findViewById<TextView>(R.id.last_seen).text = lastSeen

        if (userImage.isNotEmpty())
            Glide.with(applicationContext).load(userImage).into(findViewById(R.id.user_image))
    }

    private fun clickListeners() {

        message.addTextChangedListener {
            if (message.text.toString().trim().isNotEmpty()) {
                sendMessage.setImageResource(R.drawable.icon_send)
                photoPicker.visibility = View.GONE
                attachments.visibility = View.GONE
            } else {
                sendMessage.setImageResource(R.drawable.icon_mic)
                photoPicker.visibility = View.VISIBLE
                attachments.visibility = View.VISIBLE
            }
        }

        sendMessage.setOnClickListener {
            if (message.text.toString().trim().isNotEmpty()) {
                sendMessage(message.text.toString())
            }
        }
    }

    private fun sendMessage(message: String) {

    }
}