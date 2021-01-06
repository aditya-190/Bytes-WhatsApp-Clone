package com.bytes.messenger.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bytes.messenger.R
import com.bytes.messenger.model.Message

class MessageAdapter(
    private var messageList: ArrayList<Message>,
    private var context: Context,
) : RecyclerView.Adapter<MessageAdapter.MessageListViewHolder>() {

    private val MESSAGE_LEFT = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListViewHolder {
        return MessageListViewHolder(
            if(viewType == MESSAGE_LEFT) {
                LayoutInflater.from(context).inflate(R.layout.single_message_left, parent, false)
            }
        else {
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

    class MessageListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message: TextView = itemView.findViewById(R.id.message)
    }
}