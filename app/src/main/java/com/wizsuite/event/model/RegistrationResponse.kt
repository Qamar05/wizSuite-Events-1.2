package com.wizsuite.event.model

data class RegistrationResponse(
    val event_url: String,
    val message: String,
    val status: Boolean
)