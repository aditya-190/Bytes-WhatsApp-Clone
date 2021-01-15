package com.bytes.messenger.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bytes.messenger.FirebaseServices
import com.bytes.messenger.adapter.ChatsAdapter
import com.bytes.messenger.databinding.FragmentChatsBinding
import com.bytes.messenger.model.ChatList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatListArrayDemo: ArrayList<ChatList>
    private lateinit var adapter: ChatsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        val view = binding.root
        initialise()
        fetchData()
        return view
    }

    private fun initialise() {
        chatListArrayDemo = ArrayList()
        adapter = ChatsAdapter(chatListArrayDemo, context!!)
        binding.recycler.layoutManager = LinearLayoutManager(context)
    }

    private fun fetchData() {
        binding.inviteFriend.visibility = View.VISIBLE
        GlobalScope.launch {
            val value = FirebaseServices.getChatList()
            if (value != null) {
                for (dataChanges in value.documentChanges) {
                    val userInfo =
                        FirebaseServices.getUserInfo(dataChanges.document["userID"].toString())
                    chatListArrayDemo.add(ChatList(userInfo!!["userID"].toString(),
                        userInfo["userName"].toString(),
                        "Demo",
                        "Anything",
                        userInfo["profileImage"].toString()))

                    withContext(Dispatchers.Main) {
                        binding.inviteFriend.visibility = View.INVISIBLE
                        adapter.notifyDataSetChanged()
                        binding.recycler.adapter = ChatsAdapter(chatListArrayDemo, context!!)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}