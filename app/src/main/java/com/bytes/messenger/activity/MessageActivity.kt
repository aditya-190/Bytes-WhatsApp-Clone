package com.bytes.messenger.activity

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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class MessageActivity : AppCompatActivity() {
    private lateinit var currentUser: FirebaseUser
    private lateinit var senderMessageDb: CollectionReference
    private lateinit var receiverMessageDb: CollectionReference
    private lateinit var binding: ActivityMessageBinding
    private lateinit var receiverID: String
    private lateinit var lastSeen: String
    private lateinit var receiverName: String
    private lateinit var userImage: String
    private lateinit var messageList: ArrayList<Message>
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialise()
        clickListeners()
        readMessages()
    }

    private fun initialise() {
        currentUser = FirebaseAuth.getInstance().currentUser!!
        senderMessageDb = FirebaseFirestore.getInstance().collection("Messages")
        receiverMessageDb = FirebaseFirestore.getInstance().collection("Messages")
        receiverName = intent.getStringExtra("userName").toString()
        lastSeen = intent.getStringExtra("userLastSeen").toString()
        userImage = intent.getStringExtra("userImage").toString()
        receiverID = intent.getStringExtra("userID").toString()
        binding.name.text = receiverName
        binding.lastSeen.text = lastSeen
        messageList = ArrayList()
        adapter = MessageAdapter(messageList, this)

        if (userImage.isNotEmpty())
            Glide.with(applicationContext).load(userImage).into(binding.image)

        binding.recycler.layoutManager =
            LinearLayoutManager(this@MessageActivity).also { layout ->
                layout.orientation = RecyclerView.VERTICAL
                layout.stackFromEnd = true
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
                sendMessage(binding.message.text.toString(), "Text")
                binding.message.text.clear()
            }
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun readMessages() {
        senderMessageDb.document(currentUser.uid).collection(receiverID).addSnapshotListener { value, _ ->
            if (value != null) {
                for (dc in value.documentChanges) {
                    messageList.add(dc.document.toObject(Message::class.java))
                }
                adapter.notifyDataSetChanged()
                binding.recycler.adapter = MessageAdapter(messageList, this)
            }
        }
    }

    private fun sendMessage(
        message: String,
        type: String,
        image: String = "",
        voiceDuration: String = "",
        voiceMessage: String = "",
    ) {
        val sendMsg = Message(currentUser.uid,
            receiverID,
            message,
            System.currentTimeMillis().toString(),
            type, image, voiceDuration, voiceMessage)

        GlobalScope.launch(Dispatchers.IO) {
            senderMessageDb.document(currentUser.uid).collection(receiverID).add(sendMsg).await()
            receiverMessageDb.document(receiverID).collection(currentUser.uid).add(sendMsg).await()
        }
    }
}