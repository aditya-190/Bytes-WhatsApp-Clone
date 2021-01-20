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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ChatsFragment : Fragment() {
    private lateinit var binding: FragmentChatsBinding
    private lateinit var chatList: ArrayList<ChatList>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        val view = binding.root
        initialise()
        fetchData()
        return view
    }

    private fun initialise() {
        chatList = ArrayList()
        binding.recycler.layoutManager = LinearLayoutManager(context)
        binding.recycler.adapter = ChatsAdapter(chatList, context!!)
    }

    private fun fetchData() {
        FirebaseDatabase.getInstance().reference.child("Users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatList.clear()
                    for (data in snapshot.children) {
                        val usersID = data.child("userID").getValue(String::class.java)
                        val usersName = data.child("userName").getValue(String::class.java)
                        val usersImage = data.child("profileImage").getValue(String::class.java)
                        val usersStatus = data.child("status").getValue(String::class.java)

                        if (usersID != FirebaseAuth.getInstance().currentUser!!.uid) {
                            chatList.add(ChatList(userID = usersID.toString(),
                                userName = usersName.toString(),
                                userImage = usersImage.toString(),
                                lastSeen = usersStatus.toString()))
                        }
                    }
                    binding.recycler.adapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}