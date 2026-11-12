package com.lostfound.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.button.MaterialButton
import com.lostfound.R
import com.lostfound.model.ClaimResponse


class AdminClaimAdapter(
    private val context: Context,
    private val claims: MutableList<ClaimResponse>,
    private val onApproveClick: (ClaimResponse) -> Unit,
    private val onRejectClick: (ClaimResponse) -> Unit
) : RecyclerView.Adapter<AdminClaimAdapter.ClaimViewHolder>() {


    // ============================================================
    // VIEW HOLDER
    // ============================================================

    class ClaimViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvClaimId: TextView =
            itemView.findViewById(
                R.id.tvAdminClaimId
            )

        val tvItemName: TextView =
            itemView.findViewById(
                R.id.tvAdminClaimItemName
            )

        val tvItemType: TextView =
            itemView.findViewById(
                R.id.tvAdminClaimItemType
            )

        val tvClaimant: TextView =
            itemView.findViewById(
                R.id.tvAdminClaimant
            )

        val tvClaimantPhone: TextView =
            itemView.findViewById(
                R.id.tvAdminClaimantPhone
            )

        val tvClaimantEmail: TextView =
            itemView.findViewById(
                R.id.tvAdminClaimantEmail
            )

        val tvOwner: TextView =
            itemView.findViewById(
                R.id.tvAdminClaimOwner
            )

        val tvClaimMessage: TextView =
            itemView.findViewById(
                R.id.tvAdminClaimMessage
            )

        val tvStatus: TextView =
            itemView.findViewById(
                R.id.tvAdminClaimStatus
            )

        val btnApprove: MaterialButton =
            itemView.findViewById(
                R.id.btnApproveClaim
            )

        val btnReject: MaterialButton =
            itemView.findViewById(
                R.id.btnRejectClaim
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
            LayoutInflater
                .from(context)
                .inflate(
                    R.layout.item_admin_claim,
                    parent,
                    false
                )

        return ClaimViewHolder(view)
    }


    // ============================================================
    // BIND VIEW HOLDER
    // ============================================================

    override fun onBindViewHolder(
        holder: ClaimViewHolder,
        position: Int
    ) {

        val claim =
            claims[position]


        // ========================================================
        // CLAIM ID
        // ========================================================

        holder.tvClaimId.text =
            "Claim #${claim.id}"


        // ========================================================
        // ITEM NAME
        // ========================================================

        holder.tvItemName.text =
            if (claim.itemName.isNullOrBlank()) {

                "Unknown Item"

            } else {

                claim.itemName
            }


        // ========================================================
        // ITEM TYPE
        // ========================================================

        holder.tvItemType.text =
            if (
                claim.itemType.equals(
                    "LOST",
                    ignoreCase = true
                )
            ) {

                "LOST ITEM"

            } else {

                "FOUND ITEM"
            }


        // ========================================================
        // CLAIMANT
        // ========================================================

        holder.tvClaimant.text =
            "Claimant: ${
                claim.claimantName
                    ?: "Unknown"
            }"


        // ========================================================
        // CLAIMANT PHONE
        // ========================================================

        holder.tvClaimantPhone.text =
            "Phone: ${
                claim.claimantPhone
                    ?: "Not available"
            }"


        // ========================================================
        // CLAIMANT EMAIL
        // ========================================================

        holder.tvClaimantEmail.text =
            "Email: ${
                claim.claimantEmail
                    ?: "Not available"
            }"


        // ========================================================
        // OWNER
        // ========================================================

        holder.tvOwner.text =
            "Item Owner: ${
                claim.ownerName
                    ?: "Unknown"
            }"


        // ========================================================
        // CLAIM MESSAGE
        // ========================================================

        holder.tvClaimMessage.text =
            if (
                claim.claimMessage.isNullOrBlank()
            ) {

                "Claim message: No message provided."

            } else {

                "Claim message: ${claim.claimMessage}"
            }


        // ========================================================
        // STATUS
        // ========================================================

        holder.tvStatus.text =
            "Status: ${
                claim.status
                    ?: "PENDING"
            }"


        // ========================================================
        // BUTTONS
        // ========================================================

        val isPending =
            claim.status.equals(
                "PENDING",
                ignoreCase = true
            )


        holder.btnApprove.isEnabled =
            isPending


        holder.btnReject.isEnabled =
            isPending


        // ========================================================
        // APPROVE
        // ========================================================

        holder.btnApprove.setOnClickListener {

            if (
                holder.bindingAdapterPosition !=
                RecyclerView.NO_POSITION
            ) {

                onApproveClick(
                    claims[
                        holder.bindingAdapterPosition
                    ]
                )
            }
        }


        // ========================================================
        // REJECT
        // ========================================================

        holder.btnReject.setOnClickListener {

            if (
                holder.bindingAdapterPosition !=
                RecyclerView.NO_POSITION
            ) {

                onRejectClick(
                    claims[
                        holder.bindingAdapterPosition
                    ]
                )
            }
        }
    }


    // ============================================================
    // ITEM COUNT
    // ============================================================

    override fun getItemCount(): Int {

        return claims.size
    }


    // ============================================================
    // UPDATE DATA
    // ============================================================

    fun updateClaims(
        newClaims: List<ClaimResponse>
    ) {

        claims.clear()

        claims.addAll(
            newClaims
        )

        notifyDataSetChanged()
    }
}