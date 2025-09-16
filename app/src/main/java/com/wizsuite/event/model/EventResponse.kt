package com.wizsuite.event.model

data class EventResponse(
    val `data`: List<EventDetailsNew>,
    val message: String,
    val status: Boolean
)