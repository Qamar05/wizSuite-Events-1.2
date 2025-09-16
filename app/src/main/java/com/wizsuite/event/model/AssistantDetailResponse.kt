package com.wizsuite.event.model

data class AssistantDetailResponse(
    val `data`: List<AssistantDetail>,
    val message: String,
    val status: Boolean
)