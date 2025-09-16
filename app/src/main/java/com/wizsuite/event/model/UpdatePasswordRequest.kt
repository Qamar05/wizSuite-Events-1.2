package com.wizsuite.event.model

data class UpdatePasswordRequest(
    val password: String,
    val user_id: String
)