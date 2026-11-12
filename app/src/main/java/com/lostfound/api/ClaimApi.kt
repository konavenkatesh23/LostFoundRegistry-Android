package com.lostfound.api

import com.lostfound.model.ClaimRequest
import com.lostfound.model.ClaimResponse

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface ClaimApi {

    // ============================================================
    // CREATE CLAIM
    // ============================================================

    @POST("api/claims")
    suspend fun createClaim(
        @Header("Authorization")
        token: String,

        @Body
        request: ClaimRequest
    ): Response<ClaimResponse>


    // ============================================================
    // GET MY CLAIMS
    // ============================================================

    @GET("api/claims/my")
    suspend fun getMyClaims(
        @Header("Authorization")
        token: String
    ): Response<List<ClaimResponse>>


    // ============================================================
    // GET RECEIVED CLAIMS
    // ============================================================
    //
    // Kept for compatibility with the backend endpoint.
    //
    // Normal users will display these claims as READ-ONLY.
    // They cannot approve or reject them.
    //
    // ============================================================

    @GET("api/claims/received")
    suspend fun getReceivedClaims(
        @Header("Authorization")
        token: String
    ): Response<List<ClaimResponse>>


    // ============================================================
    // GET PENDING CLAIMS
    // ============================================================
    //
    // ADMIN ONLY
    //
    // ============================================================

    @GET("api/claims/pending")
    suspend fun getPendingClaims(
        @Header("Authorization")
        token: String
    ): Response<List<ClaimResponse>>


    // ============================================================
    // APPROVE CLAIM
    // ============================================================
    //
    // ADMIN ONLY
    //
    // ============================================================

    @PUT("api/claims/{id}/approve")
    suspend fun approveClaim(
        @Header("Authorization")
        token: String,

        @Path("id")
        claimId: Long
    ): Response<ClaimResponse>


    // ============================================================
    // REJECT CLAIM
    // ============================================================
    //
    // ADMIN ONLY
    //
    // ============================================================

    @PUT("api/claims/{id}/reject")
    suspend fun rejectClaim(
        @Header("Authorization")
        token: String,

        @Path("id")
        claimId: Long
    ): Response<ClaimResponse>
}