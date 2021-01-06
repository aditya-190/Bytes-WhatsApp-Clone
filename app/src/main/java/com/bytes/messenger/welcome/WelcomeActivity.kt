package com.bytes.messenger.welcome

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bytes.messenger.R

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        clickListeners()
    }

    private fun clickListeners() {
        findViewById<Button>(R.id.accept_n_continue).setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, PhoneLoginActivity::class.java))
        }
    }
}