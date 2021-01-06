package com.bytes.messenger.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bytes.messenger.adapter.ChatsAdapter
import com.bytes.messenger.databinding.FragmentChatsBinding
import com.bytes.messenger.model.ChatList

class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.recycler.also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = ChatsAdapter(fetchData(), context!!)
        }
        return view
    }

    private fun fetchData(): ArrayList<ChatList> {
        val chatListArrayDemo: ArrayList<ChatList> = ArrayList()
        if (chatListArrayDemo.size == 0)
            binding.inviteFriend.visibility = View.VISIBLE
        return chatListArrayDemo
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}