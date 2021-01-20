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
import com.bytes.messenger.R
import com.bytes.messenger.databinding.ActivityProfileBinding
import com.bytes.messenger.databinding.ChangeProfileInfoBinding
import com.bytes.messenger.model.User
import com.bytes.messenger.welcome.WelcomeActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var bottomSheet: BottomSheetBehavior<LinearLayout>
    private val openGallery = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomSheet = BottomSheetBehavior.from(binding.profilePicker)
        getUserInfo()
        clickListeners()
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
                        updateInfo("userName", newInfo.text.trim().toString(), null)
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
                    it.hint = getString(R.string.enter_bio)
                    it.inputType = InputType.TYPE_TEXT_VARIATION_PHONETIC
                }

                dialogBinding.cancelButton.setOnClickListener {
                    dialog.dismiss()
                }

                dialogBinding.saveButton.setOnClickListener {
                    if (newInfo.text.trim().toString().isNotEmpty())
                        updateInfo("bio", newInfo.text.trim().toString(), null)
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
        FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userInfo: User? = snapshot.getValue(User::class.java)
                    if (userInfo != null) {
                        binding.name.text = userInfo.userName
                        binding.bio.text = userInfo.bio
                        binding.number.text = userInfo.userPhone
                        val imageUrl = userInfo.profileImage
                        if (imageUrl.isNotEmpty()) Glide.with(applicationContext).load(imageUrl)
                            .into(binding.image)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun updateInfo(type: String, newInfo: String?, imageUri: Uri?) {
        binding.progressBar.visibility = View.VISIBLE
        when (type) {
            "userName" -> {
                FirebaseDatabase.getInstance().reference.child("Users")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(hashMapOf(
                        "userName" to (newInfo) as Any
                    ))
                binding.progressBar.visibility = View.INVISIBLE
            }

            "bio" -> {
                FirebaseDatabase.getInstance().reference.child("Users")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(hashMapOf(
                        "bio" to (newInfo) as Any
                    ))
                binding.progressBar.visibility = View.INVISIBLE
            }

            "PhoneNumber" -> {
            }

            "profileImage" -> {
                if (imageUri != null) {
                    val imageHolder =
                        FirebaseStorage.getInstance().reference.child("Profiles/${binding.number.text}-${FirebaseAuth.getInstance().currentUser!!.uid}")
                    imageHolder.putFile(imageUri).addOnCompleteListener {
                        if (it.isSuccessful) {
                            imageHolder.downloadUrl.addOnSuccessListener { uri ->
                                FirebaseDatabase.getInstance().reference.child("Users")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .updateChildren(hashMapOf("profileImage" to (uri.toString()) as Any))

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    binding.image.setImageBitmap(ImageDecoder.decodeBitmap(
                                        ImageDecoder.createSource(this@ProfileActivity.contentResolver,
                                            imageUri)))
                                }
                                binding.progressBar.visibility = View.INVISIBLE
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
            updateInfo("profileImage", null, imageUri)
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                "No Photo Selected.",
                Snackbar.LENGTH_SHORT).show()
        }
    }
}