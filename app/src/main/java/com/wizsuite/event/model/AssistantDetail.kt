package com.wizsuite.event.model

data class AssistantDetail(
    val contact_no: String,
    val created_at: String,
    val designation: String,
    val id: String,
    val name: String,
    val profile_image: String?,
    val support_type: String,
    val whatsapp_no: String
)