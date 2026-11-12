package com.lostfound.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.card.MaterialCardView
import com.lostfound.R
import com.lostfound.model.ClaimResponse


class ClaimAdapter(

    private val context: Context,

    private val items:
    MutableList<ClaimDisplayItem>

) : RecyclerView.Adapter<ClaimAdapter.ClaimViewHolder>() {


    // ============================================================
    // DISPLAY MODEL
    // ============================================================

    data class ClaimDisplayItem(

        val claim:
        ClaimResponse,

        val isReceived:
        Boolean
    )


    // ============================================================
    // VIEW HOLDER
    // ============================================================

    class ClaimViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val card:
                MaterialCardView =
            itemView.findViewById(
                R.id.cardClaim
            )

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

        val tvClaimant:
                TextView =
            itemView.findViewById(
                R.id.tvClaimant
            )

        val tvOwner:
                TextView =
            itemView.findViewById(
                R.id.tvOwner
            )

        val tvMessage:
                TextView =
            itemView.findViewById(
                R.id.tvClaimMessage
            )

        val tvStatus:
                TextView =
            itemView.findViewById(
                R.id.tvClaimStatus
            )

        val tvDate:
                TextView =
            itemView.findViewById(
                R.id.tvClaimDate
            )
    }


    // ============================================================
    // CREATE VIEW HOLDER
    // ============================================================

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClaimViewHolder {

        val view =
            LayoutInflater.from(
                context
            ).inflate(
                R.layout.item_claim,
                parent,
                false
            )


        return ClaimViewHolder(
            view
        )
    }


    // ============================================================
    // BIND
    // ============================================================

    override fun onBindViewHolder(
        holder: ClaimViewHolder,
        position: Int
    ) {

        val displayItem =
            items[position]

        val claim =
            displayItem.claim


        // ========================================================
        // ITEM NAME
        // ========================================================

        holder.tvItemName.text =
            claim.itemName


        // ========================================================
        // ITEM TYPE
        // ========================================================

        holder.tvItemType.text =
            claim.itemType


        // ========================================================
        // CLAIMANT
        // ========================================================

        holder.tvClaimant.text =
            "Claimant: ${claim.claimantName}"


        // ========================================================
        // OWNER
        // ========================================================

        holder.tvOwner.text =
            "Owner: ${claim.ownerName}"


        // ========================================================
        // MESSAGE
        // ========================================================

        holder.tvMessage.text =
            if (
                claim.claimMessage.isNullOrBlank()
            ) {

                "No claim message provided."

            } else {

                claim.claimMessage
            }


        // ========================================================
        // STATUS
        // ========================================================

        holder.tvStatus.text =
            "Status: ${claim.status}"


        // ========================================================
        // DATE
        // ========================================================

        holder.tvDate.text =
            "Submitted: ${claim.createdAt ?: "N/A"}"
    }


    // ============================================================
    // ITEM COUNT
    // ============================================================

    override fun getItemCount():
            Int {

        return items.size
    }
}