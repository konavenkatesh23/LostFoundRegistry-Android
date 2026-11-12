package com.lostfound.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.lostfound.ImageViewerActivity
import com.lostfound.R
import com.lostfound.model.LostItem
import com.lostfound.network.RetrofitClient

class LostItemAdapter(
    private val lostItems: List<LostItem>
) : RecyclerView.Adapter<LostItemAdapter.LostItemViewHolder>() {

    class LostItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val card: MaterialCardView =
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

        val tvStatus: TextView =
            itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LostItemViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lost, parent, false)

        return LostItemViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: LostItemViewHolder,
        position: Int
    ) {

        val item = lostItems[position]

        holder.tvItemName.text = item.itemName

        holder.tvCategory.text =
            "Category : ${item.category}"

        holder.tvColor.text =
            "Color : ${item.color ?: "N/A"}"

        holder.tvLocation.text =
            "Location : ${item.locationLost}"

        holder.tvDate.text =
            "Lost Date : ${item.dateLost}"

        holder.tvStatus.text =
            "Status : ${item.status}"

        // Build complete image URL
        val imageUrl =
            if (!item.imageUrl.isNullOrEmpty()) {
                RetrofitClient.IMAGE_BASE_URL + item.imageUrl
            } else {
                null
            }

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .centerCrop()
            .into(holder.ivItemImage)

        holder.ivItemImage.setOnClickListener {

            if (!imageUrl.isNullOrEmpty()) {

                val intent = Intent(
                    holder.itemView.context,
                    ImageViewerActivity::class.java
                )

                intent.putExtra(
                    "imageUrl",
                    imageUrl
                )

                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return lostItems.size
    }
}