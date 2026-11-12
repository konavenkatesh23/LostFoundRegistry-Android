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
import com.lostfound.model.FoundItem
import com.lostfound.network.RetrofitClient

class FoundItemAdapter(
    private val foundItems: List<FoundItem>
) : RecyclerView.Adapter<FoundItemAdapter.FoundItemViewHolder>() {

    class FoundItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

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
    ): FoundItemViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_found, parent, false)

        return FoundItemViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: FoundItemViewHolder,
        position: Int
    ) {

        val item = foundItems[position]

        holder.tvItemName.text = item.itemName

        holder.tvCategory.text =
            "Category : ${item.category}"

        holder.tvColor.text =
            "Color : ${item.color ?: "N/A"}"

        holder.tvLocation.text =
            "Location : ${item.locationFound}"

        holder.tvDate.text =
            "Found Date : ${item.dateFound}"

        holder.tvStatus.text =
            "Status : ${item.status}"

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
        return foundItems.size
    }
}