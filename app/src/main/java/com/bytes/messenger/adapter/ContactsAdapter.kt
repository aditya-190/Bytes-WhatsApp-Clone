package com.bytes.messenger.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bytes.messenger.activity.MessageActivity
import com.bytes.messenger.R
import com.bytes.messenger.model.Contact

class ContactsAdapter(
    private var contact: ArrayList<Contact>,
    private var context: Context,
) : RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        return ContactsViewHolder(
            LayoutInflater.from(context).inflate(R.layout.single_contact, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val current: Contact = contact[position]

        holder.contactName.text = current.contactName
        holder.contactBio.text = current.contactBio

        val imageUrl = current.contactImage
        if (imageUrl != "")
            Glide.with(context).load(imageUrl).centerCrop().into(holder.contactImage)

        holder.singleContact.setOnClickListener {
            context.startActivity(Intent(context, MessageActivity::class.java).also {
                it.putExtra("userID", current.contactID)
                it.putExtra("userName", current.contactName)
                it.putExtra("userBio", current.contactBio)
                it.putExtra("userImage", current.contactImage)
                it.putExtra("userLastSeen", current.lastSeen)
            })
        }
    }

    override fun getItemCount(): Int {
        return contact.size
    }

    class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var contactImage: ImageView = itemView.findViewById(R.id.contact_image)
        var contactName: TextView = itemView.findViewById(R.id.contact_name)
        var contactBio: TextView = itemView.findViewById(R.id.contact_bio)
        var singleContact: ConstraintLayout = itemView.findViewById(R.id.single_contact)
    }
}