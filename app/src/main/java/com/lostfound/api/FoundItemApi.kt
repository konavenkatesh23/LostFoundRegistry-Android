package com.lostfound.api

import com.lostfound.model.FoundItem

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface FoundItemApi {

    // ============================================================
    // CREATE FOUND ITEM
    // ============================================================

    @POST("api/found-items")
    fun reportFoundItem(
        @Header("Authorization") authorization: String,
        @Body request: Any
    ): Call<FoundItem>


    // ============================================================
    // GET ALL FOUND ITEMS
    // ============================================================

    @GET("api/found-items")
    fun getAllFoundItems(
        @Header("Authorization") authorization: String
    ): Call<List<FoundItem>>


    // ============================================================
    // GET MY FOUND ITEMS
    // ============================================================

    @GET("api/found-items/my")
    fun getMyFoundItems(
        @Header("Authorization") authorization: String
    ): Call<List<FoundItem>>


    // ============================================================
    // GET FOUND ITEM BY ID
    // ============================================================

    @GET("api/found-items/{id}")
    fun getFoundItemById(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long
    ): Call<FoundItem>


    // ============================================================
    // UPDATE FOUND ITEM
    // ============================================================

    @PUT("api/found-items/{id}")
    fun updateFoundItem(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long,
        @Body request: Any
    ): Call<FoundItem>


    // ============================================================
    // DELETE OWN FOUND ITEM
    // ============================================================

    @DELETE("api/found-items/{id}")
    fun deleteFoundItem(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long
    ): Call<String>


    // ============================================================
    // ADMIN - GET USER FOUND REPORTS
    // ============================================================

    @GET("api/found-items/admin/user/{userId}")
    fun getAdminFoundItemsByUser(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: Long
    ): Call<List<FoundItem>>


    // ============================================================
    // ADMIN - DELETE USER FOUND REPORT
    // ============================================================

    @DELETE("api/found-items/admin/user/{userId}/{itemId}")
    fun deleteAdminFoundItem(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: Long,
        @Path("itemId") itemId: Long
    ): Call<String>
}