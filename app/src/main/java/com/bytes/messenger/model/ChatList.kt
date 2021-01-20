package com.bytes.messenger.model

data class ChatList(
    var userID: String = "",
    var userName: String = "",
    var recentMessage: String = "",
    var messageTime: String = "",
    var userImage: String = "",
    var lastSeen: String = ""
)