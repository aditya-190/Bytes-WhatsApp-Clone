package com.bytes.messenger.model

data class Contact(
    var contactID: String = "",
    var contactName: String = "",
    var contactBio: String = "",
    var contactImage: String = "",
    var lastSeen: String = "",
)
