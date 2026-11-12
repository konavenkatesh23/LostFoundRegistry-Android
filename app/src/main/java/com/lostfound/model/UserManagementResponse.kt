package com.lostfound.model

data class UserManagementResponse(

    val id: Long,

    val fullName: String?,

    val email: String?,

    val phone: String?,

    val profileImageUrl: String?,

    val address: String?,

    val role: String?,

    val status: String?,

    val createdAt: String?,

    val updatedAt: String?
)