package com.wizsuite.event.model

data class NotificationResponse(
    val data: List<Notification>,
    val message: String,
    val status: Boolean
)