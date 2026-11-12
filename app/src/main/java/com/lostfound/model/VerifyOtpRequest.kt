package com.lostfound.model

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)