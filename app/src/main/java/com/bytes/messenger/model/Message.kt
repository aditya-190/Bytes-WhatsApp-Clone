package com.bytes.messenger.model

data class Message(
    var id: String = "",
    var sender: String = "",
    var receiver: String = "",
    var message: String = "",
    var time: String = "",
    var type: String = "",
    var imageUrl: String = "",
    var voiceDuration: String = "",
    var voiceMessage: String = "",
    var feelings: Int = -1,
)
