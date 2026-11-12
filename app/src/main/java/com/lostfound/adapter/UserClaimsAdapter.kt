package com.lostfound.adapter


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.lostfound.R
import com.lostfound.model.ClaimResponse


class UserClaimsAdapter(

    private val context: Context,

    private val claims:
    MutableList<ClaimResponse>

) : RecyclerView.Adapter<UserClaimsAdapter.ClaimViewHolder>() {


    // ============================================================
    // VIEW HOLDER
    // ============================================================

    class ClaimViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(
        itemView
    ) {

        val tvItemName:
                TextView =
            itemView.findViewById(
                R.id.tvClaimItemName
            )


        val tvItemType:
                TextView =
            itemView.findViewById(
                R.id.tvClaimItemType
            )


        val tvStatus:
                TextView =
            itemView.findViewById(
                R.id.tvClaimStatus
            )


        val tvOwner:
                TextView =
            itemView.findViewById(
                R.id.tvClaimOwner
            )


        val tvOwnerPhone:
                TextView =
            itemView.findViewById(
                R.id.tvClaimOwnerPhone
            )


        val tvMessage:
                TextView =
            itemView.findViewById(
                R.id.tvClaimMessage
            )


        val tvInfo:
                TextView =
            itemView.findViewById(
                R.id.tvClaimInfo
            )
    }


    // ============================================================
    // CREATE VIEW HOLDER
    // ============================================================

    override fun onCreateViewHolder(

        parent:
        ViewGroup,

        viewType:
        Int

    ): ClaimViewHolder {

        val view =
            LayoutInflater
                .from(
                    context
                )
                .inflate(
                    R.layout.item_user_claim,
                    parent,
                    false
                )


        return ClaimViewHolder(
            view
        )
    }


    // ============================================================
    // BIND VIEW HOLDER
    // ============================================================

    override fun onBindViewHolder(

        holder:
        ClaimViewHolder,

        position:
        Int

    ) {

        val claim =
            claims[position]


        // ========================================================
        // ITEM NAME
        // ========================================================

        holder.tvItemName.text =
            if (
                claim.itemName.isNullOrBlank()
            ) {

                "Unknown Item"

            } else {

                claim.itemName
            }


        // ========================================================
        // ITEM TYPE
        // ========================================================

        holder.tvItemType.text =
            "Claim Type: " +
                    (
                            claim.itemType
                                ?: "UNKNOWN"
                            ).uppercase()


        // ========================================================
        // STATUS
        // ========================================================

        val status =
            claim.status
                ?.uppercase()
                ?: "PENDING"


        holder.tvStatus.text =
            status


        // ========================================================
        // STATUS APPEARANCE
        // ========================================================

        when (
            status
        ) {

            "APPROVED" -> {

                holder.tvStatus.setTextColor(
                    Color.rgb(
                        46,
                        125,
                        50
                    )
                )
            }


            "REJECTED" -> {

                holder.tvStatus.setTextColor(
                    Color.rgb(
                        198,
                        40,
                        40
                    )
                )
            }


            else -> {

                holder.tvStatus.setTextColor(
                    Color.rgb(
                        245,
                        124,
                        0
                    )
                )
            }
        }


        // ========================================================
        // OWNER
        // ========================================================

        holder.tvOwner.text =
            "Item Owner: " +
                    (
                            claim.ownerName
                                ?: "Not available"
                            )


        // ========================================================
        // OWNER PHONE
        // ========================================================

        holder.tvOwnerPhone.text =
            "Owner Phone: " +
                    (
                            claim.ownerPhone
                                ?: "Not available"
                            )


        // ========================================================
        // CLAIM MESSAGE
        // ========================================================

        holder.tvMessage.text =
            if (
                claim.claimMessage.isNullOrBlank()
            ) {

                "Claim message: Not provided"

            } else {

                "Claim message: " +
                        claim.claimMessage
            }


        // ========================================================
        // STATUS INFORMATION
        // ========================================================

        holder.tvInfo.text =
            when (
                status
            ) {

                "APPROVED" ->
                    "Your claim has been approved by the administrator."

                "REJECTED" ->
                    "Your claim has been rejected by the administrator."

                else ->
                    "Your claim is pending administrator verification."
            }


        // ========================================================
        // IMPORTANT
        // ========================================================
        //
        // There are intentionally NO:
        //
        // Approve
        // Reject
        //
        // buttons here.
        //
        // Normal users can only view their claim information.
        //
        // ========================================================
    }


    // ============================================================
    // ITEM COUNT
    // ============================================================

    override fun getItemCount():
            Int {

        return claims.size
    }
}