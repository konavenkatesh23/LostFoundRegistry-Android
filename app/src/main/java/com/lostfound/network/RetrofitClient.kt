package com.lostfound.network

import com.lostfound.api.AuthApi
import com.lostfound.api.ClaimApi
import com.lostfound.api.FoundItemApi
import com.lostfound.api.LostItemApi
import com.lostfound.api.NotificationApi
import com.lostfound.api.ProfileApi
import com.lostfound.api.ProfileImageApi
import com.lostfound.api.UploadApi
import com.lostfound.api.UserApi

import okhttp3.OkHttpClient

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

import java.util.concurrent.TimeUnit


object RetrofitClient {

    // ============================================================
    // BACKEND CONFIGURATION
    // ============================================================

    private const val BASE_URL =
        "http://10.40.228.182:8080/"


    // ============================================================
    // IMAGE BASE URL
    // ============================================================

    const val IMAGE_BASE_URL =
        "http://10.40.228.182:8080"


    // ============================================================
    // OKHTTP CLIENT
    // ============================================================

    private val okHttpClient =
        OkHttpClient.Builder()

            .connectTimeout(
                30,
                TimeUnit.SECONDS
            )

            .readTimeout(
                30,
                TimeUnit.SECONDS
            )

            .writeTimeout(
                30,
                TimeUnit.SECONDS
            )

            .build()


    // ============================================================
    // RETROFIT INSTANCE
    // ============================================================

    private val retrofit =
        Retrofit.Builder()

            .baseUrl(BASE_URL)

            .client(okHttpClient)

            .addConverterFactory(
                ScalarsConverterFactory.create()
            )

            .addConverterFactory(
                GsonConverterFactory.create()
            )

            .build()


    // ============================================================
    // AUTH API
    // ============================================================

    val authApi: AuthApi =
        retrofit.create(
            AuthApi::class.java
        )


    // ============================================================
    // LOST ITEM API
    // ============================================================

    val lostItemApi: LostItemApi =
        retrofit.create(
            LostItemApi::class.java
        )


    // ============================================================
    // FOUND ITEM API
    // ============================================================

    val foundItemApi: FoundItemApi =
        retrofit.create(
            FoundItemApi::class.java
        )


    // ============================================================
    // GENERIC IMAGE UPLOAD API
    // ============================================================

    val uploadApi: UploadApi =
        retrofit.create(
            UploadApi::class.java
        )


    // ============================================================
    // PROFILE API
    // ============================================================

    val profileApi: ProfileApi =
        retrofit.create(
            ProfileApi::class.java
        )


    // ============================================================
    // PROFILE IMAGE API
    // ============================================================

    val profileImageApi: ProfileImageApi =
        retrofit.create(
            ProfileImageApi::class.java
        )


    // ============================================================
    // CLAIM API
    // ============================================================

    val claimApi: ClaimApi =
        retrofit.create(
            ClaimApi::class.java
        )


    // ============================================================
    // USER MANAGEMENT API
    // ============================================================

    val userApi: UserApi =
        retrofit.create(
            UserApi::class.java
        )


    // ============================================================
    // NOTIFICATION / REMINDER API
    // ============================================================

    val notificationApi: NotificationApi =
        retrofit.create(
            NotificationApi::class.java
        )
}