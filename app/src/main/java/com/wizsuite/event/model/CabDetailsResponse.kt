package com.wizsuite.event.model

data class CabDetailsResponse(
    val cab_detail: List<CabDetail>,
    val message: String,
    val status: Boolean
)