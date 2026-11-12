package com.lostfound.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton

import com.lostfound.R
import com.lostfound.model.FoundItem
import com.lostfound.model.LostItem
import com.lostfound.network.RetrofitClient


class AdminReportAdapter<T>(
    private val items: MutableList<T>,
    private val isLost: Boolean,
    private val onDeleteClick: (T) -> Unit
) : RecyclerView.Adapter<AdminReportAdapter<T>.ReportViewHolder>() {


    // ============================================================
    // VIEW HOLDER
    // ============================================================

    inner class ReportViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {


        val image: ImageView =
            itemView.findViewById(
                R.id.ivReportImage
            )


        val tvItemName: TextView =
            itemView.findViewById(
                R.id.tvReportItemName
            )


        val tvCategory: TextView =
            itemView.findViewById(
                R.id.tvReportCategory
            )


        val tvDescription: TextView =
            itemView.findViewById(
                R.id.tvReportDescription
            )


        val tvColor: TextView =
            itemView.findViewById(
                R.id.tvReportColor
            )


        val tvLocation: TextView =
            itemView.findViewById(
                R.id.tvReportLocation
            )


        val tvDate: TextView =
            itemView.findViewById(
                R.id.tvReportDate
            )


        val tvStatus: TextView =
            itemView.findViewById(
                R.id.tvReportStatus
            )


        val btnDelete: MaterialButton =
            itemView.findViewById(
                R.id.btnDeleteReport
            )
    }


    // ============================================================
    // CREATE VIEW HOLDER
    // ============================================================

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReportViewHolder {

        val view =
            LayoutInflater.from(
                parent.context
            ).inflate(
                R.layout.item_admin_report,
                parent,
                false
            )


        return ReportViewHolder(
            view
        )
    }


    // ============================================================
    // BIND
    // ============================================================

    override fun onBindViewHolder(
        holder: ReportViewHolder,
        position: Int
    ) {

        val item =
            items[position]


        // ========================================================
        // LOST ITEM
        // ========================================================

        if (
            isLost &&
            item is LostItem
        ) {

            bindLostItem(
                holder,
                item
            )
        }


        // ========================================================
        // FOUND ITEM
        // ========================================================

        else if (
            !isLost &&
            item is FoundItem
        ) {

            bindFoundItem(
                holder,
                item
            )
        }


        // ========================================================
        // DELETE
        // ========================================================

        holder.btnDelete.setOnClickListener {

            onDeleteClick(
                item
            )
        }
    }


    // ============================================================
    // BIND LOST ITEM
    // ============================================================

    private fun bindLostItem(
        holder: ReportViewHolder,
        item: LostItem
    ) {

        holder.tvItemName.text =
            item.itemName


        holder.tvCategory.text =
            "Category: ${item.category}"


        holder.tvDescription.text =
            "Description: ${
                item.description
                    ?: "Not provided"
            }"


        holder.tvColor.text =
            "Color: ${
                item.color
                    ?: "Not specified"
            }"


        holder.tvLocation.text =
            "Lost Location: ${item.locationLost}"


        holder.tvDate.text =
            "Lost Date: ${item.dateLost}"


        holder.tvStatus.text =
            item.status


        loadImage(
            holder,
            item.imageUrl
        )
    }


    // ============================================================
    // BIND FOUND ITEM
    // ============================================================

    private fun bindFoundItem(
        holder: ReportViewHolder,
        item: FoundItem
    ) {

        holder.tvItemName.text =
            item.itemName


        holder.tvCategory.text =
            "Category: ${item.category}"


        holder.tvDescription.text =
            "Description: ${
                item.description
                    ?: "Not provided"
            }"


        holder.tvColor.text =
            "Color: ${
                item.color
                    ?: "Not specified"
            }"


        holder.tvLocation.text =
            "Found Location: ${item.locationFound}"


        holder.tvDate.text =
            "Found Date: ${item.dateFound}"


        holder.tvStatus.text =
            item.status


        loadImage(
            holder,
            item.imageUrl
        )
    }


    // ============================================================
    // LOAD IMAGE
    // ============================================================

    private fun loadImage(
        holder: ReportViewHolder,
        imageUrl: String?
    ) {

        if (
            imageUrl.isNullOrBlank()
        ) {

            holder.image.visibility =
                View.GONE

            return
        }


        holder.image.visibility =
            View.VISIBLE


        val cleanUrl =
            imageUrl.trim()


        val finalUrl =
            if (
                cleanUrl.startsWith(
                    "http://",
                    ignoreCase = true
                ) ||
                cleanUrl.startsWith(
                    "https://",
                    ignoreCase = true
                )
            ) {

                cleanUrl

            } else {

                if (
                    cleanUrl.startsWith("/")
                ) {

                    RetrofitClient.IMAGE_BASE_URL +
                            cleanUrl

                } else {

                    RetrofitClient.IMAGE_BASE_URL +
                            "/" +
                            cleanUrl
                }
            }


        Glide.with(
            holder.itemView.context
        )
            .load(finalUrl)
            .centerCrop()
            .placeholder(
                android.R.drawable.ic_menu_gallery
            )
            .error(
                android.R.drawable.ic_menu_report_image
            )
            .into(
                holder.image
            )
    }


    // ============================================================
    // ITEM COUNT
    // ============================================================

    override fun getItemCount(): Int {

        return items.size
    }


    // ============================================================
    // UPDATE ITEMS
    // ============================================================

    fun updateItems(
        newItems: List<T>
    ) {

        items.clear()

        items.addAll(
            newItems
        )

        notifyDataSetChanged()
    }
}