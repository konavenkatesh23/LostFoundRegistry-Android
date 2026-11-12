package com.lostfound.model

data class FoundItemRequest(

    val itemName: String,

    val category: String,

    val description: String?,

    val color: String?,

    val locationFound: String,

    val dateFound: String,

    val imageUrl: String?
)