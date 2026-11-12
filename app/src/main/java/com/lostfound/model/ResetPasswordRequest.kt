package com.lostfound.model

data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)