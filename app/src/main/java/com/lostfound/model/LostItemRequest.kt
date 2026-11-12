package com.lostfound.model

data class LostItemRequest(

    val itemName: String,

    val category: String,

    val description: String,

    val color: String,

    val locationLost: String,

    val dateLost: String,

    val imageUrl: String?
)