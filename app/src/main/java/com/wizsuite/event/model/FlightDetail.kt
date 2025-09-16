package com.wizsuite.event.model

data class FlightDetail(
    val flight_type: String,
    val checkin_date: String,
    val checkin_link: String,
    val checkin_time: String,
    val checkout_date: String,
    val checkout_time: String,
    val description: String,
    val flight_name: String,
    val flight_no: String,
    val from_location: String,
    val layover: String,
    val pnr_no: String,
    val to_location: String
)