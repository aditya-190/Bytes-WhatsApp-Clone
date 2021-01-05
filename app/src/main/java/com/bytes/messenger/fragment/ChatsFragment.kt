package com.bytes.messenger.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytes.messenger.R
import com.bytes.messenger.adapter.ChatsAdapter
import com.bytes.messenger.model.ChatList

class ChatsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_chats, container, false)
        view.findViewById<RecyclerView>(R.id.chats_recycler).also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = ChatsAdapter(fetchData(view), context!!)
        }
        return view
    }

    private fun fetchData(view: View): ArrayList<ChatList> {
        val chatListArrayDemo: ArrayList<ChatList> = ArrayList()
        if (chatListArrayDemo.size == 0)
            view.findViewById<ConstraintLayout>(R.id.invite_friend_container).visibility =
                View.VISIBLE

        return chatListArrayDemo
    }
}