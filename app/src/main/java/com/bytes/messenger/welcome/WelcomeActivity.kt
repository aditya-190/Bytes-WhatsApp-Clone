package com.bytes.messenger.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bytes.messenger.R
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        next_button.setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, PhoneLoginActivity::class.java))
        }
    }
}