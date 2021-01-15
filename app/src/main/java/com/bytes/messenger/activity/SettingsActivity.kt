package com.bytes.messenger.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bytes.messenger.FirebaseServices
import com.bytes.messenger.databinding.ActivitySettingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getUserInfo()
        clickListeners()
    }

    private fun clickListeners() {
        binding.profileContainer.setOnClickListener {
            startActivity(Intent(this@SettingsActivity, ProfileActivity::class.java))
            finish()
        }
    }

    private fun getUserInfo() {
        GlobalScope.launch(Dispatchers.IO) {

            val reference = FirebaseServices.getUserInfo()

            withContext(Dispatchers.Main) {
                if (reference != null) {
                    binding.name.text = reference.get("userName").toString()
                    binding.bio.text = reference.get("bio").toString()

                    val imageUrl = reference.get("profileImage").toString()
                    if (imageUrl.isNotEmpty())
                        Glide.with(applicationContext).load(imageUrl).into(binding.image)
                }
            }
        }
    }
}