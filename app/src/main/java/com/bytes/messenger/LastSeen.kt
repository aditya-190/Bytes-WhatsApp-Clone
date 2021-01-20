package com.bytes.messenger

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class LastSeen {
    companion object {
        fun updateUserStatusInfo(trigger: String) {
            val updates: HashMap<String, Any> = if (trigger == "Online") {
                hashMapOf(
                    "status" to (trigger) as Any
                )
            } else {
                hashMapOf(
                    "status" to (System.currentTimeMillis().toString()) as Any
                )
            }
            FirebaseDatabase.getInstance().reference.child("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(updates)
        }
    }
}