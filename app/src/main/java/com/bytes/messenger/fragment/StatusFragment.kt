package com.bytes.messenger.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bytes.messenger.FirebaseServices
import com.bytes.messenger.databinding.FragmentStatusBinding
import kotlinx.coroutines.*

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
        GlobalScope.launch(Dispatchers.IO) {
            val imageUrl = FirebaseServices.getUserInfo()?.get("profileImage").toString()
            withContext(Dispatchers.Main) {
                if (imageUrl.isNotEmpty()) Glide.with(this@StatusFragment).load(imageUrl)
                    .into(binding.image)
            }
        }
    }
}