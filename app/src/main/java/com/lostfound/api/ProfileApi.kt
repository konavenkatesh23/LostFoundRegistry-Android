package com.lostfound.api

import com.lostfound.model.ProfileResponse
import com.lostfound.model.ProfileUpdateRequest
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface ProfileApi {

    // ============================================================
    // GET PROFILE
    // ============================================================

    @GET("api/profile")
    fun getProfile(
        @Header("Authorization")
        token: String
    ): Call<ProfileResponse>


    // ============================================================
    // UPDATE PROFILE DETAILS
    // ============================================================
    //
    // Editable:
    // - Phone
    // - Address
    //
    // Profile image URL is kept for compatibility.
    //
    // ============================================================

    @PUT("api/profile")
    fun updateProfile(
        @Header("Authorization")
        token: String,

        @Body
        request: ProfileUpdateRequest
    ): Call<ProfileResponse>


    // ============================================================
    // REPLACE PROFILE IMAGE
    // ============================================================
    //
    // PUT /api/profile/image
    //
    // Backend automatically:
    //
    // NEW IMAGE → database update → OLD IMAGE DELETE
    //
    // ============================================================

    @Multipart
    @PUT("api/profile/image")
    fun replaceProfileImage(
        @Header("Authorization")
        token: String,

        @Part
        image: MultipartBody.Part
    ): Call<ProfileResponse>
}