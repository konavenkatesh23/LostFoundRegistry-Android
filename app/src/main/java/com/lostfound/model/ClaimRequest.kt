package com.lostfound.model

import com.google.gson.annotations.SerializedName


data class ClaimRequest(

    // ============================================================
    // ITEM ID
    // ============================================================

    @SerializedName("itemId")
    val itemId: Long,


    // ============================================================
    // ITEM TYPE
    // ============================================================
    //
    // LOST
    // FOUND
    //
    // ============================================================

    @SerializedName("itemType")
    val itemType: String,


    // ============================================================
    // CLAIM MESSAGE
    // ============================================================

    @SerializedName("claimMessage")
    val claimMessage: String?
)