package com.example.adaproject.models.response

data class VideoResError(
    val error: String,
    val message: String,
    val path: String,
    val status: Int,
    val timestamp: String
)