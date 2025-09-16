package com.wizsuite.event.model

data class AssignedData(
    val assistance_team_id: String,
    val cab_id_in: String,
    val cab_id_out: String,
    val created_at: String,
    val events_id: String,
    val flight_in: String,
    val flight_out: String,
    val id: String,
    val user_id: String
)