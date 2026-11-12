package com.lostfound.api

import com.lostfound.model.ForgotPasswordRequest
import com.lostfound.model.LoginRequest
import com.lostfound.model.LoginResponse
import com.lostfound.model.RegisterRequest
import com.lostfound.model.ResetPasswordRequest
import com.lostfound.model.VerifyOtpRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    // ============================================================
    // Login
    // ============================================================

    @POST("api/auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>


    // ============================================================
    // Register
    // ============================================================

    @POST("api/auth/register")
    fun register(
        @Body request: RegisterRequest
    ): Call<String>


    // ============================================================
    // Forgot Password - Send OTP
    // ============================================================

    @POST("api/auth/forgot-password")
    fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Call<String>


    // ============================================================
    // Verify OTP
    // ============================================================

    @POST("api/auth/verify-otp")
    fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): Call<String>


    // ============================================================
    // Reset Password
    // ============================================================

    @POST("api/auth/reset-password")
    fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Call<String>
}