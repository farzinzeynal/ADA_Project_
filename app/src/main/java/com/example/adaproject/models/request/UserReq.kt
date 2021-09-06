package com.example.adaproject.models.request

data class UserReq(
    val child: Child,
    val email: String,
    val privacyContract: Boolean,
    val reportContract: Boolean
)