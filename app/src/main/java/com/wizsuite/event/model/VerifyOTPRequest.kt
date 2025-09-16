package com.wizsuite.event.model

data class VerifyOTPRequest(
    val email: String,
    val otp: String
)