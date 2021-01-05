package com.bytes.messenger.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bytes.messenger.R

class MessageActivity : AppCompatActivity() {

    private lateinit var userID: String
    private lateinit var userName: String
    private lateinit var userLastSeen: String
    private lateinit var userImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        initialise()
        clickListeners()
    }

    private fun initialise() {
        userID = intent.getStringExtra("userID").toString()
        userName = intent.getStringExtra("userName").toString()
        userLastSeen = intent.getStringExtra("userLastSeen").toString()
        userImage = intent.getStringExtra("userImage").toString()

        findViewById<TextView>(R.id.user_name).text = userName
        findViewById<TextView>(R.id.last_seen).text = userLastSeen

        if (userImage != "")
            Glide.with(applicationContext).load(userImage).into(findViewById(R.id.user_image))
    }

    private fun clickListeners() {
    }
}