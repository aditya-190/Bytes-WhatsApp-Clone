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
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bytes.messenger.R
import com.bytes.messenger.welcome.WelcomeActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class ProfileActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userName: TextView
    private lateinit var userBio: TextView
    private lateinit var userNumber: TextView
    private lateinit var userImage: ImageView
    private lateinit var profileLayout: LinearLayout
    private lateinit var bottomSheetBehaviourPicker: BottomSheetBehavior<LinearLayout>
    private val openGallery = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initialise()
        getUserInfo()
        clickListeners()
    }

    private fun initialise() {
        userName = findViewById(R.id.user_name)
        userBio = findViewById(R.id.user_bio)
        userNumber = findViewById(R.id.user_number)
        userImage = findViewById(R.id.user_image)

        firestore = FirebaseFirestore.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        profileLayout = findViewById(R.id.profile_picker)
        bottomSheetBehaviourPicker = BottomSheetBehavior.from(profileLayout)
    }

    private fun clickListeners() {

        findViewById<ImageView>(R.id.edit_username).setOnClickListener {
            Dialog(this@ProfileActivity).also { dialog ->
                dialog.setContentView(R.layout.change_profile_info)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                dialog.findViewById<TextView>(R.id.change_info_heading).text =
                    getString(R.string.enter_name)
                val newInfo: EditText = dialog.findViewById<EditText>(R.id.new_info).also {
                    it.hint = "Name Goes Here..."
                    it.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                }

                dialog.findViewById<TextView>(R.id.cancel_button).setOnClickListener {
                    dialog.dismiss()
                }

                dialog.findViewById<TextView>(R.id.save_button).setOnClickListener {
                    updateInfo("Name", newInfo.text.trim().toString(), null)
                    dialog.dismiss()
                }
                dialog.show()
            }
        }

        findViewById<ImageView>(R.id.edit_bio).setOnClickListener {
            Dialog(this@ProfileActivity).also { dialog ->
                dialog.setContentView(R.layout.change_profile_info)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                dialog.findViewById<TextView>(R.id.change_info_heading).text =
                    getString(R.string.bio)
                val newInfo: EditText = dialog.findViewById<EditText>(R.id.new_info).also {
                    it.hint = "Here goes the Bio..."
                    it.inputType = InputType.TYPE_TEXT_VARIATION_PHONETIC
                }

                dialog.findViewById<TextView>(R.id.cancel_button).setOnClickListener {
                    dialog.dismiss()
                }

                dialog.findViewById<TextView>(R.id.save_button).setOnClickListener {
                    updateInfo("Bio", newInfo.text.trim().toString(), null)
                    dialog.dismiss()
                }
                dialog.show()
            }
        }

        findViewById<ImageView>(R.id.edit_phone_number).setOnClickListener {
            Snackbar.make(findViewById(android.R.id.content),
                "Feature Pending.",
                Snackbar.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.sign_out).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@ProfileActivity, WelcomeActivity::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.gallery_picker).setOnClickListener {
            Intent().also {
                it.type = "image/*"
                it.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(it, "Select Image"),
                    openGallery)
            }
            bottomSheetBehaviourPicker.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        findViewById<FloatingActionButton>(R.id.changeProfile).setOnClickListener {
            if (bottomSheetBehaviourPicker.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehaviourPicker.state = BottomSheetBehavior.STATE_COLLAPSED
            else
                bottomSheetBehaviourPicker.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun getUserInfo() {
        firestore.collection("Users").document(firebaseUser.uid).get().addOnSuccessListener {
            userName.text = it.get("userName").toString()
            userBio.text = it.get("bio").toString()
            userNumber.text = it.get("userPhone").toString()
            val imageUrl = it.get("profileImage").toString()
            if (imageUrl != "") Glide.with(applicationContext).load(imageUrl).into(userImage)
        }.addOnFailureListener {
            Snackbar.make(findViewById(android.R.id.content),
                "Something went wrong.",
                Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun updateInfo(type: String, newInfo: String?, imageUri: Uri?) {
        when (type) {
            "Name" -> {
                firestore.collection("Users").document(firebaseUser.uid)
                    .update("userName", newInfo).addOnSuccessListener {
                        getUserInfo()
                    }.addOnFailureListener {
                        Snackbar.make(findViewById(android.R.id.content),
                            "Something went wrong.",
                            Snackbar.LENGTH_SHORT).show()
                    }
            }
            "Bio" -> {
                firestore.collection("Users").document(firebaseUser.uid)
                    .update("bio", newInfo).addOnSuccessListener {
                        getUserInfo()
                    }.addOnFailureListener {
                        Snackbar.make(findViewById(android.R.id.content),
                            "Something went wrong.",
                            Snackbar.LENGTH_SHORT).show()
                    }
            }
            "PhoneNumber" -> {
            }
            "Image" -> {
                val storageReference: StorageReference =
                    FirebaseStorage.getInstance().reference.child("Profiles/${userNumber.text}-${firebaseUser.uid}")
                if (imageUri != null) {
                    storageReference.putFile(imageUri).addOnSuccessListener {
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            Log.d("URI", "onSuccess: uri= $uri")
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
            userImage.setImageBitmap(bitmap)
            updateInfo("Image", null, imageUri)
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                "No Photo Selected.",
                Snackbar.LENGTH_SHORT).show()
        }
    }
}