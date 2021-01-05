package com.bytes.messenger

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bytes.messenger.welcome.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
    }

    private fun redirect() {
        if ((FirebaseAuth.getInstance().currentUser != null)) {
            Handler(Looper.myLooper()!!).postDelayed({
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                finish()
            }, 600)
        } else {
            startActivity(Intent(this@SplashScreen, WelcomeActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        redirect()
    }
}