package com.bytes.messenger.model

data class Status(
    var userID: String = "",
    var statusID: String = "",
    var dateCreated: String = "",
    var image: String = "",
    var text: String = "",
    var views: String = "",
)
