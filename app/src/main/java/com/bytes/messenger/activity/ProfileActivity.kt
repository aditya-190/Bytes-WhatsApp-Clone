package com.bytes.messenger.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bytes.messenger.R
import com.bytes.messenger.welcome.WelcomeActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.change_profile_info.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class ProfileActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firestore: FirebaseFirestore
    private lateinit var fireStoreReference: DocumentReference
    private lateinit var bottomSheet: BottomSheetBehavior<LinearLayout>
    private val openGallery = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initialise()
        getUserInfo()
        clickListeners()
    }

    private fun initialise() {
        firestore = FirebaseFirestore.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        fireStoreReference = firestore.collection("Users").document(firebaseUser.uid)
        bottomSheet = BottomSheetBehavior.from(profile_picker)
    }

    private fun clickListeners() {
        edit_name.setOnClickListener {
            Dialog(this@ProfileActivity).also { dialog ->
                dialog.setContentView(R.layout.change_profile_info)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.change_info_heading.text =
                    getString(R.string.enter_name)
                val newInfo: EditText = dialog.new_info.also {
                    it.hint = "Name Goes Here..."
                    it.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                }

                dialog.cancel_button.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.save_button.setOnClickListener {
                    if (newInfo.text.trim().toString().isNotEmpty())
                        updateInfo("Name", newInfo.text.trim().toString(), null)
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
        edit_bio.setOnClickListener {
            Dialog(this@ProfileActivity).also { dialog ->
                dialog.setContentView(R.layout.change_profile_info)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.change_info_heading.text =
                    getString(R.string.bio)
                val newInfo: EditText = dialog.new_info.also {
                    it.hint = "Here goes the Bio..."
                    it.inputType = InputType.TYPE_TEXT_VARIATION_PHONETIC
                }

                dialog.cancel_button.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.save_button.setOnClickListener {
                    if (newInfo.text.trim().toString().isNotEmpty())
                        updateInfo("Bio", newInfo.text.trim().toString(), null)
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
        edit_number.setOnClickListener {
            Snackbar.make(findViewById(android.R.id.content),
                "Feature Pending.",
                Snackbar.LENGTH_SHORT).show()
        }
        sign_out.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@ProfileActivity, WelcomeActivity::class.java))
            finish()
        }

        gallery_picker.setOnClickListener {
            Intent().also {
                it.type = "image/*"
                it.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(it, "Select Image"),
                    openGallery)
            }
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        change.setOnClickListener {
            if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            else
                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun getUserInfo() {
        GlobalScope.launch(Dispatchers.IO) {
            val it = fireStoreReference.get().await()
            withContext(Dispatchers.Main) {
                name.text = it.get("userName").toString()
                bio.text = it.get("bio").toString()
                number.text = it.get("userPhone").toString()

                val imageUrl = it.get("profileImage").toString()
                if (imageUrl.isNotEmpty()) Glide.with(applicationContext).load(imageUrl)
                    .into(image)
            }
        }
    }

    private fun updateInfo(type: String, newInfo: String?, imageUri: Uri?) {
        when (type) {
            "Name" -> {
                GlobalScope.launch(Dispatchers.IO) {
                    fireStoreReference.update("userName", newInfo).await()
                    withContext(Dispatchers.Main) {
                        getUserInfo()
                    }
                }
            }
            "Bio" -> {
                GlobalScope.launch(Dispatchers.IO) {
                    fireStoreReference.update("bio", newInfo).await()
                    withContext(Dispatchers.Main) {
                        getUserInfo()
                    }
                }

            }
            "PhoneNumber" -> {
            }
            "Image" -> {
                if (imageUri != null) {
                    val cloudReference: StorageReference =
                        FirebaseStorage.getInstance().reference.child("Profiles/${number.text}-${firebaseUser.uid}")
                    cloudReference.putFile(imageUri).addOnSuccessListener {
                        cloudReference.downloadUrl.addOnSuccessListener { uri ->
                            firestore.collection("Users").document(firebaseUser.uid)
                                .update("profileImage", uri.toString()).addOnSuccessListener {
                                    getUserInfo()
                                }.addOnFailureListener {
                                    Snackbar.make(findViewById(android.R.id.content),
                                        "Something went wrong.",
                                        Snackbar.LENGTH_SHORT).show()
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

            val bitmap = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(this.contentResolver,
                        imageUri))

                else -> MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            }
            image.setImageBitmap(bitmap)
            updateInfo("Image", null, imageUri)
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                "No Photo Selected.",
                Snackbar.LENGTH_SHORT).show()
        }
    }
}