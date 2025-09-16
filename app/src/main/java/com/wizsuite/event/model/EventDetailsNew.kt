package com.wizsuite.event.model

data class EventDetailsNew(
    val assignId: String,
    val assistant: String,
    val created_at: String,
    val description: String,
    val event_end_time: String,
    val event_image: String,
    val event_start_time: String,
    val events_end_date: String,
    val events_name: String,
    val events_start_date: String,
    val id: String,
    val latitude: String,
    val longitude: String,
    val upload_agenda: String,
    val venue_address: String,
    val venue_image: String,
    val venue_link: String,
    val venue_name: String
)