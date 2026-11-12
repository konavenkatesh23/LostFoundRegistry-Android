package com.lostfound.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

import com.lostfound.ImageViewerActivity
import com.lostfound.R
import com.lostfound.network.RetrofitClient


class MyReportsAdapter(
    private val context: Context,
    private var reportItems: MutableList<ReportItem>,
    private val onEditClick: (ReportItem) -> Unit,
    private val onDeleteClick: (ReportItem, Int) -> Unit
) : RecyclerView.Adapter<MyReportsAdapter.MyReportViewHolder>() {


    // ============================================================
    // COMMON REPORT MODEL
    // ============================================================

    data class ReportItem(

        val id: Long,

        val itemName: String,

        val category: String,

        val description: String?,

        val color: String?,

        val location: String,

        val date: String,

        val imageUrl: String?,

        val isLost: Boolean,

        // NEW:
        // Used to determine whether Edit/Delete are allowed.
        val status: String? = null
    )


    // ============================================================
    // VIEW HOLDER
    // ============================================================

    class MyReportViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val cardItem: MaterialCardView =
            itemView.findViewById(R.id.cardItem)

        val ivItemImage: ImageView =
            itemView.findViewById(R.id.ivItemImage)

        val tvItemName: TextView =
            itemView.findViewById(R.id.tvItemName)

        val tvCategory: TextView =
            itemView.findViewById(R.id.tvCategory)

        val tvColor: TextView =
            itemView.findViewById(R.id.tvColor)

        val tvLocation: TextView =
            itemView.findViewById(R.id.tvLocation)

        val tvDate: TextView =
            itemView.findViewById(R.id.tvDate)

        val btnEdit: MaterialButton =
            itemView.findViewById(R.id.btnEdit)

        val btnDelete: MaterialButton =
            itemView.findViewById(R.id.btnDelete)
    }


    // ============================================================
    // CREATE VIEW HOLDER
    // ============================================================

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyReportViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_my_report,
                    parent,
                    false
                )

        return MyReportViewHolder(view)
    }


    // ============================================================
    // BIND VIEW HOLDER
    // ============================================================

    override fun onBindViewHolder(
        holder: MyReportViewHolder,
        position: Int
    ) {

        val item =
            reportItems[position]


        // ========================================================
        // ITEM NAME
        // ========================================================

        holder.tvItemName.text =
            item.itemName


        // ========================================================
        // CATEGORY
        // ========================================================

        holder.tvCategory.text =
            "Category : ${item.category}"


        // ========================================================
        // COLOR
        // ========================================================

        holder.tvColor.text =
            "Color : ${item.color ?: "Not specified"}"


        // ========================================================
        // LOCATION
        // ========================================================

        holder.tvLocation.text =
            "Location : ${item.location}"


        // ========================================================
        // DATE
        // ========================================================

        holder.tvDate.text =
            if (item.isLost) {

                "Lost Date : ${item.date}"

            } else {

                "Found Date : ${item.date}"
            }


        // ========================================================
        // IMAGE URL
        // ========================================================

        val imageUrl =
            buildImageUrl(
                item.imageUrl
            )


        // ========================================================
        // LOAD IMAGE
        // ========================================================

        Glide.with(
            holder.itemView.context
        )
            .load(imageUrl)
            .placeholder(
                R.drawable.ic_launcher_foreground
            )
            .error(
                R.drawable.ic_launcher_foreground
            )
            .centerCrop()
            .into(
                holder.ivItemImage
            )


        // ========================================================
        // FULL IMAGE VIEWER
        // ========================================================

        holder.ivItemImage.setOnClickListener {

            if (!imageUrl.isNullOrBlank()) {

                val intent =
                    Intent(
                        holder.itemView.context,
                        ImageViewerActivity::class.java
                    )

                intent.putExtra(
                    "imageUrl",
                    imageUrl
                )

                holder.itemView.context
                    .startActivity(intent)
            }
        }


        // ========================================================
        // CLAIMED STATUS CHECK
        // ========================================================

        val isClaimed =
            item.status.equals(
                "CLAIMED",
                ignoreCase = true
            )


        // ========================================================
        // EDIT / DELETE VISIBILITY
        // ========================================================
        //
        // LOST:
        //     Normal Edit/Delete behaviour.
        //
        // FOUND:
        //     FOUND   -> Edit/Delete visible
        //     CLAIMED -> Edit/Delete hidden
        //
        // This is only the UI protection.
        // Backend protection is also implemented in
        // FoundItemService.java.
        //
        // ========================================================

        if (isClaimed) {

            holder.btnEdit.visibility =
                View.GONE

            holder.btnDelete.visibility =
                View.GONE

        } else {

            holder.btnEdit.visibility =
                View.VISIBLE

            holder.btnDelete.visibility =
                View.VISIBLE
        }


        // ========================================================
        // EDIT
        // ========================================================

        holder.btnEdit.setOnClickListener {

            // Extra safety check.
            if (item.status.equals(
                    "CLAIMED",
                    ignoreCase = true
                )
            ) {

                return@setOnClickListener
            }

            onEditClick(
                item
            )
        }


        // ========================================================
        // DELETE
        // ========================================================

        holder.btnDelete.setOnClickListener {

            // Extra safety check.
            if (item.status.equals(
                    "CLAIMED",
                    ignoreCase = true
                )
            ) {

                return@setOnClickListener
            }

            val adapterPosition =
                holder.bindingAdapterPosition

            if (
                adapterPosition !=
                RecyclerView.NO_POSITION
            ) {

                onDeleteClick(
                    item,
                    adapterPosition
                )
            }
        }
    }


    // ============================================================
    // BUILD IMAGE URL
    // ============================================================

    private fun buildImageUrl(
        imageUrl: String?
    ): String? {

        if (imageUrl.isNullOrBlank()) {
            return null
        }

        return when {

            imageUrl.startsWith(
                "http://"
            ) -> {

                imageUrl
            }

            imageUrl.startsWith(
                "https://"
            ) -> {

                imageUrl
            }

            imageUrl.startsWith("/") -> {

                RetrofitClient.IMAGE_BASE_URL +
                        imageUrl
            }

            else -> {

                RetrofitClient.IMAGE_BASE_URL +
                        "/" +
                        imageUrl
            }
        }
    }


    // ============================================================
    // ITEM COUNT
    // ============================================================

    override fun getItemCount(): Int {

        return reportItems.size
    }


    // ============================================================
    // REMOVE ITEM
    // ============================================================

    fun removeItem(
        position: Int
    ) {

        if (
            position >= 0 &&
            position < reportItems.size
        ) {

            reportItems.removeAt(
                position
            )

            notifyItemRemoved(
                position
            )
        }
    }


    // ============================================================
    // UPDATE ITEMS
    // ============================================================

    fun updateItems(
        newItems: List<ReportItem>
    ) {

        reportItems.clear()

        reportItems.addAll(
            newItems
        )

        notifyDataSetChanged()
    }
}