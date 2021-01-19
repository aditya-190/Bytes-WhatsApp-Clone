package com.bytes.messenger.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bytes.messenger.R
import com.bytes.messenger.adapter.MessageAdapter
import com.bytes.messenger.databinding.ActivityMessageBinding
import com.bytes.messenger.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessageBinding
    private lateinit var receiverID: String
    private lateinit var lastSeen: String
    private lateinit var receiverName: String
    private lateinit var userImage: String
    private lateinit var senderMsgReference: String
    private lateinit var receiverMsgReference: String
    private lateinit var messageList: ArrayList<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialise()
        clickListeners()
        readMessages()
    }

    private fun initialise() {
        receiverName = intent.getStringExtra("userName").toString()
        lastSeen = intent.getStringExtra("userLastSeen").toString()
        userImage = intent.getStringExtra("userImage").toString()
        receiverID = intent.getStringExtra("userID").toString()
        binding.name.text = receiverName
        binding.lastSeen.text = lastSeen
        messageList = ArrayList()
        senderMsgReference = FirebaseAuth.getInstance().currentUser!!.uid
        receiverMsgReference = receiverID

        if (userImage.isNotEmpty())
            Glide.with(applicationContext).load(userImage).into(binding.image)

        binding.recycler.also {
            it.layoutManager = LinearLayoutManager(this@MessageActivity).also { layout ->
                layout.orientation = RecyclerView.VERTICAL
                layout.stackFromEnd = true
            }
            it.adapter = MessageAdapter(messageList, this, senderMsgReference, receiverMsgReference)
        }
    }

    private fun clickListeners() {

        binding.message.addTextChangedListener {
            if (binding.message.text.toString().trim().isNotEmpty()) {
                binding.sendButton.setImageResource(R.drawable.icon_send)
                binding.photos.visibility = View.GONE
                binding.attachment.visibility = View.GONE
            } else {
                binding.sendButton.setImageResource(R.drawable.icon_mic)
                binding.photos.visibility = View.VISIBLE
                binding.attachment.visibility = View.VISIBLE
            }
        }

        binding.sendButton.setOnClickListener {
            if (binding.message.text.toString().trim().isNotEmpty()) {
                sendMessage(message = binding.message.text.trim().toString(), type = "Text")
                binding.message.text.clear()
            }
        }

        binding.attachment.setOnClickListener {
            if (binding.attachmentMenu.visibility == View.VISIBLE)
                binding.attachmentMenu.visibility = View.GONE
            else
                binding.attachmentMenu.visibility = View.VISIBLE
        }

        binding.image.setOnClickListener {
            startActivity(Intent(this@MessageActivity, ReceiverProfileActivity::class.java).also {
                it.putExtra("userID", receiverID)
                it.putExtra("userName", receiverName)
                it.putExtra("userImage", userImage)
                it.putExtra("lastSeen", lastSeen)
            })
            finish()
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun readMessages() {
        FirebaseDatabase.getInstance().reference.child("Messages")
            .child(senderMsgReference).child("All")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (data in snapshot.children) {
                        val message: Message? = data.getValue(Message::class.java)
                        if (message != null) {
                            message.id = data.key.toString()
                            messageList.add(message)
                        }
                    }
                    binding.recycler.adapter?.notifyDataSetChanged()
                    binding.recycler.smoothScrollToPosition(messageList.size)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun sendMessage(
        messageID: String = "",
        message: String,
        type: String,
        image: String = "",
        voiceDuration: String = "",
        voiceMessage: String = "",
        feelings: Int = -1,
    ) {
        val getTime =
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date().time).toString()
                .replace("am", "AM").replace("pm", "PM")

        val sendMsg = Message(id = messageID, sender = FirebaseAuth.getInstance().currentUser!!.uid,
            receiver = receiverID,
            message = message,
            time = getTime,
            type = type,
            imageUrl = image,
            voiceDuration = voiceDuration,
            voiceMessage = voiceMessage,
            feelings = feelings)

        val messageKey: String = FirebaseDatabase.getInstance().reference.push().key.toString()

        val recentMsg = hashMapOf(
            "recentMessage" to (message) as Any,
            "recentMsgTime" to (getTime) as Any,
        )

        FirebaseDatabase.getInstance().reference.child("Messages")
            .child(senderMsgReference).child("LastMsg").updateChildren(recentMsg)
            .addOnSuccessListener {
                FirebaseDatabase.getInstance().reference.child("Messages")
                    .child(receiverMsgReference).child("LastMsg").updateChildren(recentMsg)
            }

        FirebaseDatabase.getInstance().reference.child("Messages")
            .child(senderMsgReference).child("All").child(messageKey).setValue(sendMsg)
            .addOnSuccessListener {
                FirebaseDatabase.getInstance().reference.child("Messages")
                    .child(receiverMsgReference).child("All").child(messageKey).setValue(sendMsg)
            }
    }
}