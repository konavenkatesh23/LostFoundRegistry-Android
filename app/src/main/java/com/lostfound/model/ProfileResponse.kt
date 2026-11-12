package com.lostfound.model

data class ProfileResponse(

    val id: Long,

    val fullName: String,

    val email: String,

    val phone: String?,

    val profileImageUrl: String?,

    val address: String?
)