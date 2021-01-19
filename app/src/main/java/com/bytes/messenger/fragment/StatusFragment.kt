package com.bytes.messenger.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bytes.messenger.databinding.FragmentStatusBinding
import com.bytes.messenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StatusFragment : Fragment() {

    private var _binding: FragmentStatusBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStatusBinding.inflate(inflater, container, false)
        val view = binding.root
        fetchData()
        return view
    }

    private fun fetchData() {
        FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val imageUrl = snapshot.getValue(User::class.java)?.profileImage
                    if (imageUrl != null) {
                        if (imageUrl.isNotEmpty()) Glide.with(this@StatusFragment).load(imageUrl)
                            .into(binding.image)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }
}