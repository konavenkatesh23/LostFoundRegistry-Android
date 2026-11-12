package com.lostfound.model

data class LoginResponse(

    val token: String,

    val message: String,

    val fullName: String,

    val role: String
)