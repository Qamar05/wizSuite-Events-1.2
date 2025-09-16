package com.wizsuite.event.model

data class Notification(
    val created_at: String,
    val events_id: Int,
    val id: Int,
    val message_body: String,
    val title: String,
    val user_id: String
)