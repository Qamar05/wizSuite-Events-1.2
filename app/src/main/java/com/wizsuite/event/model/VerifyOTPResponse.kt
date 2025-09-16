package com.wizsuite.event.model

data class VerifyOTPResponse(
    val message: String,
    val status: Boolean,
    val user_id: String
)