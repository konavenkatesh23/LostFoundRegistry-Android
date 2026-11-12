package com.lostfound.model

data class LostItem(

    val id: Long,

    val itemName: String,

    val category: String,

    val description: String?,

    val color: String?,

    val locationLost: String,

    val dateLost: String,

    val imageUrl: String?,

    val status: String,

    val createdAt: String?,

    val userId: Long?,

    val ownerId: Long?
)