package com.bytes.messenger

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.bytes.messenger.activity.MessageActivity
import com.bytes.messenger.adapter.MessageAdapter
import com.bytes.messenger.model.Message
import com.bytes.messenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseServices {
    companion object {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        private val usersDb = FirebaseFirestore.getInstance().collection("Users")
        val messagesDb = FirebaseFirestore.getInstance().collection("Messages")

        suspend fun addUser(userDetails: User) {
            usersDb.document(currentUser!!.uid).set(userDetails).await()
        }

        suspend fun getUserInfo(userID: String = currentUser!!.uid): DocumentSnapshot? {
            return usersDb.document(userID).get().await()
        }

        suspend fun getChatList(): QuerySnapshot? {
            return messagesDb.document(currentUser!!.uid).collection("ChatList").get().await()
        }

        suspend fun getContactsList(): QuerySnapshot? {
            return usersDb.get().await()
        }

        suspend fun sendMsg(receiverID: String, msg: Message) {
            messagesDb.document(currentUser!!.uid).collection("Messages").document(receiverID)
                .collection("AllUserMessages").add(msg).await()

            messagesDb.document(currentUser.uid).collection("ChatList").document(receiverID)
                .set(hashMapOf("userID" to receiverID)).await()

            messagesDb.document(receiverID).collection("Messages").document(currentUser.uid)
                .collection("AllUserMessages").add(msg).await()

            messagesDb.document(receiverID).collection("ChatList").document(currentUser.uid)
                .set(hashMapOf("userID" to currentUser.uid)).await()
        }

        suspend fun update(change: String, newInfo: String?) {
            usersDb.document(currentUser!!.uid).update(change, newInfo).await()
        }

        suspend fun storeImage(number: String, imageUri: Uri): Uri? {
            val imageHolder =
                FirebaseStorage.getInstance().reference.child("Profiles/${number}-${currentUser!!.uid}")

            imageHolder.putFile(imageUri).await()
            return imageHolder.downloadUrl.await()
        }
    }
}