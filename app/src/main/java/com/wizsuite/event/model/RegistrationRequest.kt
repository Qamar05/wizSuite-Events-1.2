package com.wizsuite.event.model

data class RegistrationRequest(
    val city: String,
    val email: String,
    val govermt_id_type: String,
    val name: String,
    val password: String,
    val phone: String,
    val t_shirt: String
)