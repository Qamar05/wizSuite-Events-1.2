package com.wizsuite.event.model

data class AllAssignedIdResponse(
    val data: AssignedData,
    val message: String,
    val status: Boolean
)