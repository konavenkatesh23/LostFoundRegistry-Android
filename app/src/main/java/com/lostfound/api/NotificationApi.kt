package com.lostfound.api

import com.lostfound.model.NotificationResponse
import com.lostfound.model.ReminderRequest

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotificationApi {

    @POST("api/notifications/reminders")
    suspend fun createReminder(
        @Header("Authorization")
        token: String,

        @Body
        request: ReminderRequest
    ): Response<NotificationResponse>

    @GET("api/notifications/my")
    suspend fun getMyNotifications(
        @Header("Authorization")
        token: String
    ): Response<List<NotificationResponse>>

    @GET("api/notifications/unread-count")
    suspend fun getUnreadCount(
        @Header("Authorization")
        token: String
    ): Response<Map<String, Long>>

    @GET("api/notifications/{id}")
    suspend fun getNotification(
        @Header("Authorization")
        token: String,

        @Path("id")
        notificationId: Long
    ): Response<NotificationResponse>

    @PUT("api/notifications/{id}/read")
    suspend fun markAsRead(
        @Header("Authorization")
        token: String,

        @Path("id")
        notificationId: Long
    ): Response<NotificationResponse>
}