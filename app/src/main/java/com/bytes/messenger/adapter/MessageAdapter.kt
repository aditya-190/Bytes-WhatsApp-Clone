package com.bytes.messenger.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bytes.messenger.R
import com.bytes.messenger.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MessageAdapter(
    private var messageList: ArrayList<Message>,
    private var context: Context,
) : RecyclerView.Adapter<MessageAdapter.MessageListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListViewHolder {
        return MessageListViewHolder(
            if (viewType == 1) {
                LayoutInflater.from(context).inflate(R.layout.single_message_left, parent, false)
            } else {
                LayoutInflater.from(context).inflate(R.layout.single_message_right, parent, false)
            }
        )
    }

    override fun onBindViewHolder(holder: MessageListViewHolder, position: Int) {
        val current: Message = messageList[position]
        holder.message.text = current.message
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].sender == FirebaseAuth.getInstance().currentUser!!.uid)
            0
        else
            1
    }

    class MessageListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message: TextView = itemView.findViewById(R.id.message)
    }
}