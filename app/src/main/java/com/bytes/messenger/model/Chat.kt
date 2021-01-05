package com.bytes.messenger.model

data class Chat(
    var time: String = "",
    var message: String = "",
    var url: String = "",
    var type: String = "",
    var sender: String = "",
    var receiver: String = "",
)
