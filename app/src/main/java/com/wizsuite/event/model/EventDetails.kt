package com.wizsuite.event.model

data class EventDetails(
    val assistance_team_id: String,
    val assistant: String,
    val cab_id_in: String,
    val cab_id_out: String,
    val created_at: String,
    val description: String,
    val event_end_time: String,
    val event_image: String,
    val event_start_time: String,
    val events_end_date: String,
    val events_id: String,
    val events_name: String,
    val events_start_date: String,
    val flight_in: String,
    val flight_out: String,
    val id: String,
    val latitude: String,
    val longitude: String,
    val upload_agenda: String,
    val user_id: String,
    val venue_address: String,
    val venue_image: String,
    val venue_link: String,
    val venue_name: String
)