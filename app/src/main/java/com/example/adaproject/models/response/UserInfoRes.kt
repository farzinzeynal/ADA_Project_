package com.example.adaproject.models.response

data class UserInfoRes(
    val child: Child,
    val email: String,
    val id: Int,
    val isReportable: IsReportable,
    val privacyContract: Boolean,
    val reportContract: Boolean
)