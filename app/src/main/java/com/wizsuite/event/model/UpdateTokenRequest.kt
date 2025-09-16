package com.wizsuite.event.model

data class UpdateTokenRequest(
    val device_id: String,
    val device_type: String,
    val token: String
)