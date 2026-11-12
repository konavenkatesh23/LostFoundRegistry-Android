package com.lostfound.api

import com.lostfound.model.LostItem

import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Part


interface LostItemApi {

    // ============================================================
    // CREATE LOST ITEM
    // ============================================================

    @POST("api/lost-items")
    fun reportLostItem(
        @Header("Authorization") authorization: String,
        @Body request: Any
    ): Call<LostItem>


    // ============================================================
    // GET ALL LOST ITEMS
    // ============================================================

    @GET("api/lost-items")
    fun getAllLostItems(
        @Header("Authorization") authorization: String
    ): Call<List<LostItem>>


    // ============================================================
    // GET MY LOST ITEMS
    // ============================================================

    @GET("api/lost-items/my")
    fun getMyLostItems(
        @Header("Authorization") authorization: String
    ): Call<List<LostItem>>


    // ============================================================
    // GET LOST ITEM BY ID
    // ============================================================

    @GET("api/lost-items/{id}")
    fun getLostItemById(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long
    ): Call<LostItem>


    // ============================================================
    // UPDATE LOST ITEM
    // ============================================================

    @PUT("api/lost-items/{id}")
    fun updateLostItem(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long,
        @Body request: Any
    ): Call<LostItem>


    // ============================================================
    // UPDATE LOST ITEM WITH IMAGE
    // ============================================================

    @Multipart
    @PUT("api/lost-items/{id}/with-image")
    fun updateLostItemWithImage(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long,

        @Part("itemName") itemName: RequestBody,
        @Part("category") category: RequestBody,
        @Part("description") description: RequestBody,
        @Part("color") color: RequestBody,
        @Part("locationLost") locationLost: RequestBody,
        @Part("dateLost") dateLost: RequestBody,

        @Part image: MultipartBody.Part
    ): Call<LostItem>


    // ============================================================
    // DELETE OWN LOST ITEM
    // ============================================================

    @DELETE("api/lost-items/{id}")
    fun deleteLostItem(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long
    ): Call<String>


    // ============================================================
    // ADMIN - GET USER LOST REPORTS
    // ============================================================

    @GET("api/lost-items/admin/user/{userId}")
    fun getAdminLostItemsByUser(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: Long
    ): Call<List<LostItem>>


    // ============================================================
    // ADMIN - DELETE USER LOST REPORT
    // ============================================================

    @DELETE("api/lost-items/admin/user/{userId}/{itemId}")
    fun deleteAdminLostItem(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: Long,
        @Path("itemId") itemId: Long
    ): Call<String>
}