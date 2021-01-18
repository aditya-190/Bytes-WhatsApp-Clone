package com.bytes.messenger.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bytes.messenger.R
import com.bytes.messenger.model.Message
import com.github.pgreze.reactions.ReactionPopup
import com.github.pgreze.reactions.dsl.reactionConfig
import com.github.pgreze.reactions.dsl.reactions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MessageAdapter(
    private var messageList: ArrayList<Message>,
    private var context: Context,
    private var senderMsgReference: String,
    private var receiverMsgReference: String,
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
        holder.time.text = current.time

        val reactionIds = intArrayOf(R.drawable.reaction_like,
            R.drawable.reaction_love,
            R.drawable.reaction_laugh,
            R.drawable.reaction_wow,
            R.drawable.reaction_sad,
            R.drawable.reaction_angry)

        val config = reactionConfig(context) {
            reactions {
                resId { R.drawable.reaction_like }
                resId { R.drawable.reaction_love }
                resId { R.drawable.reaction_laugh }
                resId { R.drawable.reaction_wow }
                resId { R.drawable.reaction_sad }
                resId { R.drawable.reaction_angry }
            }
        }

        val popup = ReactionPopup(context, config) { pos ->
            if (pos >= 0) {
                holder.feelings.setImageResource(reactionIds[pos])
                holder.feelings.visibility = View.VISIBLE
            } else {
                holder.feelings.visibility = View.INVISIBLE
            }
            current.feelings = pos
            val updates = hashMapOf(
                "feelings" to (pos) as Any
            )

            FirebaseDatabase.getInstance().reference.child("Messages").child(senderMsgReference)
                .child("All").child(current.id).updateChildren(updates)
            FirebaseDatabase.getInstance().reference.child("Messages").child(receiverMsgReference)
                .child("All").child(current.id).updateChildren(updates)
            true
        }

        holder.messageContainer.setOnTouchListener(popup)

        if (current.feelings >= 0) {
            holder.feelings.setImageResource(reactionIds[current.feelings])
            holder.feelings.visibility = View.VISIBLE
        } else {
            holder.feelings.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].sender == FirebaseAuth.getInstance().currentUser!!.uid) 0
        else 1
    }

    class MessageListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageContainer: ConstraintLayout = itemView.findViewById(R.id.message_container)
        val message: TextView = itemView.findViewById(R.id.message)
        val time: TextView = itemView.findViewById(R.id.message_time)
        val feelings: ImageView = itemView.findViewById(R.id.feelings)
    }
}