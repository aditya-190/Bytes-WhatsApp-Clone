package com.bytes.messenger.model

data class User(
    var userID: String,
    var userName: String,
    var userPhone: String,
    var status: String,
    var bio: String,
    var profileImage: String,
    var coverImage: String,
)
