package com.lostfound.model

import com.google.gson.annotations.SerializedName


data class ClaimResponse(

    // ============================================================
    // CLAIM ID
    // ============================================================

    @SerializedName("id")
    val id: Long,


    // ============================================================
    // ITEM ID
    // ============================================================
    //
    // ID of the claimed Lost or Found item.
    //
    // ============================================================

    @SerializedName("itemId")
    val itemId: Long,


    // ============================================================
    // ITEM TYPE
    // ============================================================
    //
    // Possible values:
    //
    // LOST
    // FOUND
    //
    // ============================================================

    @SerializedName("itemType")
    val itemType: String,


    // ============================================================
    // ITEM NAME
    // ============================================================

    @SerializedName("itemName")
    val itemName: String,


    // ============================================================
    // CLAIMANT
    // ============================================================

    @SerializedName("claimantId")
    val claimantId: Long,

    @SerializedName("claimantName")
    val claimantName: String?,

    @SerializedName("claimantPhone")
    val claimantPhone: String?,

    @SerializedName("claimantEmail")
    val claimantEmail: String?,


    // ============================================================
    // ITEM OWNER / UPLOADER
    // ============================================================

    @SerializedName("ownerId")
    val ownerId: Long,

    @SerializedName("ownerName")
    val ownerName: String?,

    @SerializedName("ownerPhone")
    val ownerPhone: String?,

    @SerializedName("ownerEmail")
    val ownerEmail: String?,


    // ============================================================
    // CLAIM MESSAGE
    // ============================================================

    @SerializedName("claimMessage")
    val claimMessage: String?,


    // ============================================================
    // CLAIM STATUS
    // ============================================================
    //
    // Possible values:
    //
    // PENDING
    // APPROVED
    // REJECTED
    //
    // ============================================================

    @SerializedName("status")
    val status: String,


    // ============================================================
    // CLAIM CREATED DATE / TIME
    // ============================================================

    @SerializedName("createdAt")
    val createdAt: String?,


    // ============================================================
    // CLAIM APPROVED DATE / TIME
    // ============================================================
    //
    // null when:
    //
    // PENDING
    // REJECTED
    //
    // Contains approval date/time when:
    //
    // APPROVED
    //
    // ============================================================

    @SerializedName("approvedAt")
    val approvedAt: String?
)