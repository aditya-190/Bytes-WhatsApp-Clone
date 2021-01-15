package com.bytes.messenger.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bytes.messenger.FirebaseServices
import com.bytes.messenger.R
import com.bytes.messenger.databinding.ActivityProfileBinding
import com.bytes.messenger.databinding.ChangeProfileInfoBinding
import com.bytes.messenger.welcome.WelcomeActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var bottomSheet: BottomSheetBehavior<LinearLayout>
    private val openGallery = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialise()
        getUserInfo()
        clickListeners()
    }

    private fun initialise() {
        bottomSheet = BottomSheetBehavior.from(binding.profilePicker)
    }

    private fun clickListeners() {
        binding.editName.setOnClickListener {
            val dialogBinding = ChangeProfileInfoBinding.inflate(layoutInflater)

            Dialog(this@ProfileActivity).also { dialog ->
                dialog.setContentView(dialogBinding.root)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialogBinding.changeInfoHeading.text =
                    getString(R.string.enter_name)
                val newInfo: EditText = dialogBinding.newInfo.also {
                    it.hint = "Name Goes Here..."
                    it.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                }

                dialogBinding.cancelButton.setOnClickListener {
                    dialog.dismiss()
                }

                dialogBinding.saveButton.setOnClickListener {
                    if (newInfo.text.trim().toString().isNotEmpty())
                        updateInfo("Name", newInfo.text.trim().toString(), null)
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
        binding.editBio.setOnClickListener {
            val dialogBinding = ChangeProfileInfoBinding.inflate(layoutInflater)
            Dialog(this@ProfileActivity).also { dialog ->
                dialog.setContentView(dialogBinding.root)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialogBinding.changeInfoHeading.text =
                    getString(R.string.bio)
                val newInfo: EditText = dialogBinding.newInfo.also {
                    it.hint = "Here goes the Bio..."
                    it.inputType = InputType.TYPE_TEXT_VARIATION_PHONETIC
                }

                dialogBinding.cancelButton.setOnClickListener {
                    dialog.dismiss()
                }

                dialogBinding.saveButton.setOnClickListener {
                    if (newInfo.text.trim().toString().isNotEmpty())
                        updateInfo("Bio", newInfo.text.trim().toString(), null)
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
        binding.editNumber.setOnClickListener {
            Snackbar.make(findViewById(android.R.id.content),
                "Feature Pending.",
                Snackbar.LENGTH_SHORT).show()
        }
        binding.signOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@ProfileActivity, WelcomeActivity::class.java))
            finish()
        }

        binding.galleryPicker.setOnClickListener {
            Intent().also {
                it.type = "image/*"
                it.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(it, "Select Image"),
                    openGallery)
            }
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.change.setOnClickListener {
            if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            else
                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun getUserInfo() {
        GlobalScope.launch(Dispatchers.IO) {

            val userInfo = FirebaseServices.getUserInfo()

            withContext(Dispatchers.Main) {

                if (userInfo != null) {
                    binding.name.text = userInfo.get("userName").toString()
                    binding.bio.text = userInfo.get("bio").toString()
                    binding.number.text = userInfo.get("userPhone").toString()

                    val imageUrl = userInfo.get("profileImage").toString()
                    if (imageUrl.isNotEmpty()) Glide.with(applicationContext).load(imageUrl)
                        .into(binding.image)
                }
            }
        }
    }

    private fun updateInfo(type: String, newInfo: String?, imageUri: Uri?) {
        binding.progressBar.visibility = View.VISIBLE

        when (type) {
            "Name" -> {
                GlobalScope.launch(Dispatchers.IO) {
                    FirebaseServices.update("userName", newInfo)
                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.INVISIBLE
                        getUserInfo()
                    }
                }
            }

            "Bio" -> {
                GlobalScope.launch(Dispatchers.IO) {
                    FirebaseServices.update("bio", newInfo)
                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.INVISIBLE
                        getUserInfo()
                    }
                }
            }

            "PhoneNumber" -> {
            }

            "Image" -> {
                if (imageUri != null) {
                    GlobalScope.launch(Dispatchers.IO) {
                        val uri =
                            FirebaseServices.storeImage(binding.number.text.toString(), imageUri)
                        FirebaseServices.update("profileImage", uri.toString())

                        withContext(Dispatchers.Main) {
                            binding.progressBar.visibility = View.INVISIBLE

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                binding.image.setImageBitmap(ImageDecoder.decodeBitmap(
                                    ImageDecoder.createSource(this@ProfileActivity.contentResolver,
                                        imageUri)))
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageUri: Uri? = data?.data
        if (requestCode == openGallery && resultCode == RESULT_OK && data != null && imageUri != null) {
            updateInfo("Image", null, imageUri)
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                "No Photo Selected.",
                Snackbar.LENGTH_SHORT).show()
        }
    }
}