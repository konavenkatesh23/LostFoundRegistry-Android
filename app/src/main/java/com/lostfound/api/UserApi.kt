package com.lostfound.api

import com.lostfound.model.UserManagementResponse

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface UserApi {

    // ============================================================
    // GET ALL USERS
    // ============================================================
    //
    // GET /api/admin/users
    //
    // Authorization:
    // Bearer <JWT>
    //
    // ============================================================

    @GET("api/admin/users")
    suspend fun getAllUsers(
        @Header("Authorization") authorization: String
    ): Response<List<UserManagementResponse>>


    // ============================================================
    // DELETE USER
    // ============================================================
    //
    // DELETE /api/admin/users/{userId}
    //
    // Authorization:
    // Bearer <JWT>
    //
    // ============================================================

    @DELETE("api/admin/users/{userId}")
    suspend fun deleteUser(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: Long
    ): Response<String>
}