package com.example.adaproject.models.response

data class Video(
    val created: String,
    val id: Int,
    val status: String,
    val title: String,
    val updated: String,
    val url: String,
    val weeks: Int
)