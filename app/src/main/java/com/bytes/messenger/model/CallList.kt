package com.bytes.messenger.model

data class CallList(
    var callerID: String = "",
    var callerName: String = "",
    var callTime: String = "",
    var called: Boolean = false,
    var missed: Boolean = false,
    var callerImage: String = "",
)
