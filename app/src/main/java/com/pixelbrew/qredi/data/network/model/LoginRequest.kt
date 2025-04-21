package com.pixelbrew.qredi.data.network.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class ApiError(
    val message: String
)