package com.lostfound.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.lostfound.model.NotificationResponse

import java.text.SimpleDateFormat
import java.util.Locale


class NotificationAdapter(
    private var items: MutableList<NotificationResponse>,
    private val onNotificationClick:
        (NotificationResponse) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {


    // ============================================================
    // VIEW HOLDER
    // ============================================================

    class NotificationViewHolder(
        val root: FrameLayout,
        val titleText: TextView,
        val messageText: TextView,
        val dateText: TextView
    ) : RecyclerView.ViewHolder(root)


    // ============================================================
    // CREATE VIEW HOLDER
    // ============================================================

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolder {

        val context = parent.context


        // ========================================================
        // ROOT CARD
        // ========================================================

        val root =
            FrameLayout(context)

        /*
         * IMPORTANT:
         *
         * RecyclerView requires RecyclerView.LayoutParams
         * for the root view.
         *
         * We do NOT cast LayoutParams to MarginLayoutParams.
         *
         * This fixes:
         *
         * ClassCastException:
         * LayoutParams cannot be cast to MarginLayoutParams
         */

        val rootParams =
            RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        root.layoutParams =
            rootParams

        root.setPadding(
            20,
            12,
            20,
            12
        )


        // ========================================================
        // CARD CONTAINER
        // ========================================================

        val card =
            LinearLayout(context)

        card.orientation =
            LinearLayout.VERTICAL

        card.setPadding(
            24,
            22,
            24,
            22
        )

        card.setBackgroundColor(
            Color.rgb(
                38,
                34,
                43
            )
        )


        /*
         * IMPORTANT:
         *
         * Use FrameLayout.LayoutParams because
         * 'card' is a child of FrameLayout.
         *
         * No unsafe MarginLayoutParams cast.
         */

        val cardParams =
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        cardParams.leftMargin =
            4

        cardParams.rightMargin =
            4

        cardParams.topMargin =
            6

        cardParams.bottomMargin =
            6

        card.layoutParams =
            cardParams


        // ========================================================
        // TITLE
        // ========================================================

        val titleText =
            TextView(context)

        titleText.textSize =
            20f

        titleText.setTextColor(
            Color.WHITE
        )

        titleText.setTypeface(
            null,
            Typeface.BOLD
        )

        titleText.maxLines =
            2


        val titleParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        titleParams.bottomMargin =
            10

        titleText.layoutParams =
            titleParams


        // ========================================================
        // MESSAGE
        // ========================================================

        val messageText =
            TextView(context)

        messageText.textSize =
            16f

        messageText.setTextColor(
            Color.rgb(
                225,
                220,
                230
            )
        )

        messageText.setLineSpacing(
            0f,
            1.15f
        )

        messageText.maxLines =
            10


        val messageParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        messageParams.bottomMargin =
            12

        messageText.layoutParams =
            messageParams


        // ========================================================
        // DATE
        // ========================================================

        val dateText =
            TextView(context)

        dateText.textSize =
            13f

        dateText.setTextColor(
            Color.rgb(
                170,
                165,
                175
            )
        )

        dateText.gravity =
            Gravity.START


        val dateParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        dateText.layoutParams =
            dateParams


        // ========================================================
        // ADD VIEWS
        // ========================================================

        card.addView(
            titleText
        )

        card.addView(
            messageText
        )

        card.addView(
            dateText
        )

        root.addView(
            card
        )


        // ========================================================
        // RETURN HOLDER
        // ========================================================

        return NotificationViewHolder(
            root,
            titleText,
            messageText,
            dateText
        )
    }


    // ============================================================
    // BIND VIEW HOLDER
    // ============================================================

    override fun onBindViewHolder(
        holder: NotificationViewHolder,
        position: Int
    ) {

        if (
            position < 0 ||
            position >= items.size
        ) {
            return
        }


        val notification =
            items[position]


        // ========================================================
        // TITLE
        // ========================================================

        holder.titleText.text =
            notification.title


        // ========================================================
        // MESSAGE
        // ========================================================

        holder.messageText.text =
            notification.message


        // ========================================================
        // DATE
        // ========================================================

        holder.dateText.text =
            formatDate(
                notification.createdAt
            )


        // ========================================================
        // UNREAD / READ APPEARANCE
        // ========================================================

        if (
            !notification.isRead
        ) {

            holder.titleText.setTextColor(
                Color.rgb(
                    206,
                    147,
                    216
                )
            )

            holder.root.setBackgroundColor(
                Color.TRANSPARENT
            )

        } else {

            holder.titleText.setTextColor(
                Color.WHITE
            )

            holder.root.setBackgroundColor(
                Color.TRANSPARENT
            )
        }


        // ========================================================
        // CLICK
        // ========================================================

        holder.root.setOnClickListener {

            onNotificationClick(
                notification
            )
        }
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
        newItems: List<NotificationResponse>
    ) {

        items.clear()

        items.addAll(
            newItems
        )

        notifyDataSetChanged()
    }


    // ============================================================
    // FORMAT DATE
    // ============================================================

    private fun formatDate(
        dateString: String?
    ): String {

        if (
            dateString.isNullOrBlank()
        ) {

            return ""
        }


        return try {

            /*
             * Backend LocalDateTime:
             *
             * 2026-10-08T15:30:45
             */

            val inputFormat =
                SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss",
                    Locale.getDefault()
                )


            val outputFormat =
                SimpleDateFormat(
                    "dd MMM yyyy, hh:mm a",
                    Locale.getDefault()
                )


            val date =
                inputFormat.parse(
                    dateString
                )


            if (date != null) {

                outputFormat.format(
                    date
                )

            } else {

                dateString
            }

        } catch (
            e: Exception
        ) {

            /*
             * If backend sends a slightly
             * different date format, simply
             * display the original value.
             */

            dateString
        }
    }
}