package com.wizsuite.event.model

data class LoginResponse(
    val email: String,
    val message: String,
    val name: String,
    val status: Boolean,
    val phone: String,
    val token: String
)