package com.bytes.messenger.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bytes.messenger.R
import com.bytes.messenger.adapter.ChatsAdapter
import com.bytes.messenger.model.ChatList
import kotlinx.android.synthetic.main.fragment_chats.view.*

class ChatsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        view.recycler.also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = ChatsAdapter(fetchData(view), context!!)
        }
        return view
    }

    private fun fetchData(view: View): ArrayList<ChatList> {
        val chatListArrayDemo: ArrayList<ChatList> = ArrayList()
        if (chatListArrayDemo.size == 0)
            view.invite_friend.visibility = View.VISIBLE
        return chatListArrayDemo
    }
}