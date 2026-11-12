package com.lostfound.model

data class FoundItem(

    val id: Long,

    val itemName: String,

    val category: String,

    val description: String?,

    val color: String?,

    val locationFound: String,

    val dateFound: String,

    val imageUrl: String?,

    val status: String,

    val createdAt: String?,

    val userId: Long?,

    val ownerId: Long?
)