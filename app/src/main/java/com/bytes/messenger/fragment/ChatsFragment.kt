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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var readMessageDb: CollectionReference
    private lateinit var getUsersDb: CollectionReference
    private lateinit var chatListArrayDemo: ArrayList<ChatList>
    private lateinit var currentUser: FirebaseUser
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
        currentUser = FirebaseAuth.getInstance().currentUser!!
        readMessageDb = FirebaseFirestore.getInstance().collection("Messages")
        getUsersDb = FirebaseFirestore.getInstance().collection("Users")
        chatListArrayDemo = ArrayList()
        adapter = ChatsAdapter(chatListArrayDemo, context!!)
        binding.recycler.layoutManager = LinearLayoutManager(context)
    }

    private fun fetchData() {
        binding.inviteFriend.visibility = View.VISIBLE
        GlobalScope.launch {
            val value =
                readMessageDb.document(currentUser.uid).collection("ChatList").get()
                    .await()
            if (value != null) {
                for (dataChanges in value.documentChanges) {
                    val userID = dataChanges.document["userID"].toString()
                    getUserData(userID)
                }
            }
        }
    }

    private fun getUserData(userID: String) {
        getUsersDb.document(userID).get().addOnSuccessListener {
            chatListArrayDemo.add(ChatList(it["userID"].toString(),
                it["userName"].toString(),
                "Demo",
                "Anything",
                it["profileImage"].toString()))

            binding.inviteFriend.visibility = View.INVISIBLE
            adapter.notifyDataSetChanged()
            binding.recycler.adapter = ChatsAdapter(chatListArrayDemo, context!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}