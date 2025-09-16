package com.wizsuite.event.model

data class CabDetail(
    val cab_no: String,
    val cab_type: String,
    val description: String,
    val driver_contact_no: String,
    val driver_name: String,
    val driver_whatsapp_no: String,
    val drop_point: String,
    val drop_time: String,
    val pick_up_date: String,
    val pickup_point: String,
    val pickup_time: String
)