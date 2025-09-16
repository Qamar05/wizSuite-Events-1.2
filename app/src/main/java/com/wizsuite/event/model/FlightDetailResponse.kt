package com.wizsuite.event.model

data class FlightDetailResponse(
    val flight_detail: List<FlightDetail>,
    val message: String,
    val status: Boolean
)