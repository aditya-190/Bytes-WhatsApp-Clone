package com.bytes.messenger.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bytes.messenger.R
import com.bytes.messenger.model.CallList

class CallsAdapter(
    private var chat: ArrayList<CallList>,
    private var context: Context,
) : RecyclerView.Adapter<CallsAdapter.CallsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallsViewHolder {
        return CallsViewHolder(
            LayoutInflater.from(context).inflate(R.layout.single_call, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CallsViewHolder, position: Int) {
        val current: CallList = chat[position]

        holder.callerName.text = current.callerName
        holder.callTime.text = current.callTime

        Glide.with(context).load(current.callerImage).centerCrop().into(holder.callerImage)

        if (current.called)
            holder.callType.setImageResource(R.drawable.icon_called)
        else
            holder.callType.setImageResource(R.drawable.icon_received)

        if (current.missed)
            holder.callType.drawable.setTint(ContextCompat.getColor(context, R.color.red))
        else
            holder.callType.drawable.setTint(ContextCompat.getColor(context, R.color.colorPrimary))

        holder.callButton.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return chat.size
    }

    class CallsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var callerImage: ImageView = itemView.findViewById(R.id.caller_image)
        var callerName: TextView = itemView.findViewById(R.id.caller_name)
        var callType: ImageView = itemView.findViewById(R.id.call_type)
        var callTime: TextView = itemView.findViewById(R.id.call_time)
        var callButton: ImageView = itemView.findViewById(R.id.call_button)
    }
}