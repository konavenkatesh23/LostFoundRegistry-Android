package com.lostfound.api

import com.lostfound.model.ProfileResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface ProfileImageApi {

    // ============================================================
    // REPLACE PROFILE IMAGE
    // ============================================================
    //
    // PUT /api/profile/image
    //
    // This endpoint is specifically for profile pictures.
    //
    // The backend will:
    //
    // 1. Find the old profile picture
    // 2. Delete the old picture
    // 3. Save the new picture
    // 4. Update the database
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