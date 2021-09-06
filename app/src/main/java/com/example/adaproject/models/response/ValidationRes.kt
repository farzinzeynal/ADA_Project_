package com.example.adaproject.models.response

data class ValidationRes(
    val expiration: String,
    val token: String,
    val validated: Boolean
)