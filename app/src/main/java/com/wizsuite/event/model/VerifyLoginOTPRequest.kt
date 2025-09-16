package com.wizsuite.event.model

data class VerifyLoginOTPRequest(
    val phone: String,
    val otp: String
)