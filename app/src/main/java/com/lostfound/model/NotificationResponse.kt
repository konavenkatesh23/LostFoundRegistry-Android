package com.lostfound.model

import com.google.gson.annotations.SerializedName

data class NotificationResponse(

    @SerializedName("id")
    val id: Long,

    @SerializedName("userId")
    val userId: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("isRead")
    val isRead: Boolean,

    @SerializedName("createdAt")
    val createdAt: String?
)