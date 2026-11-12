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
import com.lostfound.SearchItem
import com.lostfound.network.RetrofitClient

class SearchItemAdapter(
    private var items: List<SearchItem>,
    private val currentUserId: Long = -1L,
    private var ownFoundItemIds: Set<Long> = emptySet(),
    private val onClaimClick: (SearchItem) -> Unit = {},
    private val onReminderClick: (SearchItem) -> Unit = {}
) : RecyclerView.Adapter<SearchItemAdapter.SearchViewHolder>() {

    class SearchViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val image: ImageView =
            itemView.findViewById(R.id.imgItem)

        val itemName: TextView =
            itemView.findViewById(R.id.tvItemName)

        val category: TextView =
            itemView.findViewById(R.id.tvCategory)

        val color: TextView =
            itemView.findViewById(R.id.tvColor)

        val location: TextView =
            itemView.findViewById(R.id.tvLocation)

        val date: TextView =
            itemView.findViewById(R.id.tvDate)

        val btnRequestClaim: MaterialButton =
            itemView.findViewById(R.id.btnRequestClaim)

        val btnReminder: MaterialButton =
            itemView.findViewById(R.id.btnReminder)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_search_result,
                    parent,
                    false
                )

        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SearchViewHolder,
        position: Int
    ) {

        val item = items[position]

        holder.itemName.text = item.itemName

        holder.category.text =
            "Category : ${item.category}"

        holder.color.text =
            "Color : ${item.color ?: "Not specified"}"

        holder.location.text =
            "Location : ${item.location}"

        holder.date.text =
            if (item.type.equals("LOST", ignoreCase = true)) {
                "Lost Date : ${item.date}"
            } else {
                "Found Date : ${item.date}"
            }

        // ------------------------------------------------------------
        // IMAGE
        // ------------------------------------------------------------

        val imageUrl =
            buildImageUrl(item.imageUrl)

        if (!imageUrl.isNullOrBlank()) {

            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .centerCrop()
                .into(holder.image)

        } else {

            holder.image.setImageResource(
                R.drawable.image_placeholder
            )
        }

        // ------------------------------------------------------------
        // RESET BUTTONS
        // ------------------------------------------------------------

        holder.btnRequestClaim.visibility =
            View.GONE

        holder.btnReminder.visibility =
            View.GONE

        holder.btnRequestClaim.setOnClickListener(null)
        holder.btnReminder.setOnClickListener(null)

        // ------------------------------------------------------------
        // OWN FOUND ITEM
        // ------------------------------------------------------------

        val isOwnFoundItem =
            item.type.equals(
                "FOUND",
                ignoreCase = true
            ) && item.id in ownFoundItemIds

        // ------------------------------------------------------------
        // OWN ITEM
        // ------------------------------------------------------------

        val isOwnItem =
            isOwnFoundItem ||
                    item.ownerId == currentUserId

        if (isOwnItem) {
            return
        }

        // ------------------------------------------------------------
        // LOST ITEM
        // ------------------------------------------------------------

        if (
            item.type.equals(
                "LOST",
                ignoreCase = true
            )
        ) {

            holder.btnReminder.visibility =
                View.VISIBLE

            holder.btnReminder.isEnabled =
                true

            holder.btnReminder.alpha =
                1.0f

            holder.btnReminder.text =
                "Remind User"

            holder.btnReminder.setOnClickListener {

                val adapterPosition =
                    holder.bindingAdapterPosition

                if (
                    adapterPosition !=
                    RecyclerView.NO_POSITION
                ) {

                    onReminderClick(
                        items[adapterPosition]
                    )
                }
            }

            return
        }

        // ------------------------------------------------------------
        // FOUND ITEM
        // ------------------------------------------------------------

        if (
            item.type.equals(
                "FOUND",
                ignoreCase = true
            )
        ) {

            holder.btnRequestClaim.visibility =
                View.VISIBLE

            holder.btnRequestClaim.isEnabled =
                true

            holder.btnRequestClaim.alpha =
                1.0f

            holder.btnRequestClaim.text =
                "Request Claim"

            holder.btnRequestClaim.setOnClickListener {

                val adapterPosition =
                    holder.bindingAdapterPosition

                if (
                    adapterPosition !=
                    RecyclerView.NO_POSITION
                ) {

                    onClaimClick(
                        items[adapterPosition]
                    )
                }
            }
        }
    }

    private fun buildImageUrl(
        imageUrl: String?
    ): String? {

        if (imageUrl.isNullOrBlank()) {
            return null
        }

        val cleanUrl =
            imageUrl.trim()

        if (
            cleanUrl.startsWith("http://") ||
            cleanUrl.startsWith("https://")
        ) {
            return cleanUrl
        }

        if (cleanUrl.startsWith("/")) {
            return RetrofitClient.IMAGE_BASE_URL +
                    cleanUrl
        }

        return "${RetrofitClient.IMAGE_BASE_URL}/$cleanUrl"
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(
        newItems: List<SearchItem>
    ) {

        items = newItems

        notifyDataSetChanged()
    }

    fun updateOwnFoundItemIds(
        newIds: Set<Long>
    ) {

        ownFoundItemIds =
            newIds

        notifyDataSetChanged()
    }
}