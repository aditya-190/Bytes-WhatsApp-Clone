package com.bytes.messenger.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bytes.messenger.R
import com.bytes.messenger.model.ChatList

class ChatsAdapter(
    private var chatList: ArrayList<ChatList>,
    private var context: Context,
) : RecyclerView.Adapter<ChatsAdapter.ChatListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        return ChatListViewHolder(
            LayoutInflater.from(context).inflate(R.layout.single_chat, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        val current: ChatList = chatList[position]

        holder.userName.text = current.userName
        holder.recentMessage.text = current.recentMessage
        holder.messageTime.text = current.messageTime
        Glide.with(context).load(current.userImage).centerCrop().into(holder.userImage)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    class ChatListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userImage: ImageView = itemView.findViewById(R.id.user_image)
        var userName: TextView = itemView.findViewById(R.id.user_name)
        var recentMessage: TextView = itemView.findViewById(R.id.recent_message)
        var messageTime: TextView = itemView.findViewById(R.id.message_time)
    }
}