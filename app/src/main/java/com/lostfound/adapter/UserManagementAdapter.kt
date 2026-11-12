package com.lostfound.adapter


import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.button.MaterialButton

import com.lostfound.R
import com.lostfound.model.UserManagementResponse
import com.lostfound.network.RetrofitClient

import java.net.HttpURLConnection
import java.net.URL

import java.util.concurrent.Executors


class UserManagementAdapter(

    private var users:
    MutableList<UserManagementResponse>,

    private val onUserClick:
        (UserManagementResponse) -> Unit,

    private val onDeleteClick:
        (UserManagementResponse) -> Unit

) : RecyclerView.Adapter<UserManagementAdapter.UserViewHolder>() {


    // ============================================================
    // IMAGE EXECUTOR
    // ============================================================

    private val imageExecutor =
        Executors.newFixedThreadPool(4)


    private val mainHandler =
        Handler(
            Looper.getMainLooper()
        )


    // ============================================================
    // VIEW HOLDER
    // ============================================================

    class UserViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {


        val ivUserProfile:
                ImageView =
            itemView.findViewById(
                R.id.ivUserProfile
            )


        val tvUserInitial:
                TextView =
            itemView.findViewById(
                R.id.tvUserInitial
            )


        val tvUserName:
                TextView =
            itemView.findViewById(
                R.id.tvUserName
            )


        val tvUserRole:
                TextView =
            itemView.findViewById(
                R.id.tvUserRole
            )


        val tvUserEmail:
                TextView =
            itemView.findViewById(
                R.id.tvUserEmail
            )


        val tvUserPhone:
                TextView =
            itemView.findViewById(
                R.id.tvUserPhone
            )


        val tvUserAddress:
                TextView =
            itemView.findViewById(
                R.id.tvUserAddress
            )


        val tvUserStatus:
                TextView =
            itemView.findViewById(
                R.id.tvUserStatus
            )


        val btnDeleteUser:
                MaterialButton =
            itemView.findViewById(
                R.id.btnDeleteUser
            )
    }


    // ============================================================
    // CREATE VIEW HOLDER
    // ============================================================

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {


        val view =
            LayoutInflater.from(
                parent.context
            ).inflate(
                R.layout.item_admin_user,
                parent,
                false
            )


        return UserViewHolder(
            view
        )
    }


    // ============================================================
    // BIND USER
    // ============================================================

    override fun onBindViewHolder(
        holder: UserViewHolder,
        position: Int
    ) {


        val user =
            users[position]


        // ========================================================
        // NAME
        // ========================================================

        val name =
            user.fullName
                ?.trim()
                ?.takeIf {
                    it.isNotEmpty()
                }
                ?: "Unknown User"


        holder.tvUserName.text =
            name


        // ========================================================
        // INITIAL
        // ========================================================

        val initial =
            name
                .firstOrNull()
                ?.uppercase()
                ?: "U"


        holder.tvUserInitial.text =
            initial


        // ========================================================
        // DEFAULT AVATAR STATE
        // ========================================================
        //
        // Initially show the alphabet.
        //
        // If an image successfully loads, the image will replace it.
        //
        // This is important because RecyclerView reuses ViewHolders.
        //
        // ========================================================

        holder.tvUserInitial.visibility =
            View.VISIBLE


        holder.ivUserProfile.visibility =
            View.GONE


        holder.ivUserProfile.setImageDrawable(
            null
        )


        // ========================================================
        // PROFILE IMAGE
        // ========================================================

        val profileImageUrl =
            user.profileImageUrl
                ?.trim()
                ?.takeIf {
                    it.isNotEmpty()
                }


        if (
            profileImageUrl != null
        ) {


            val completeImageUrl =
                buildImageUrl(
                    profileImageUrl
                )


            // ----------------------------------------------------
            // Store URL on ImageView.
            //
            // This prevents an old asynchronous image request
            // from appearing in a recycled ViewHolder.
            // ----------------------------------------------------

            holder.ivUserProfile.tag =
                completeImageUrl


            imageExecutor.execute {


                val bitmap =
                    downloadBitmap(
                        completeImageUrl
                    )


                mainHandler.post {


                    // ------------------------------------------------
                    // Make sure this ImageView is still displaying
                    // the same user's image.
                    // ------------------------------------------------

                    if (
                        holder.ivUserProfile.tag
                        != completeImageUrl
                    ) {

                        return@post
                    }


                    if (
                        bitmap != null
                    ) {

                        holder.ivUserProfile
                            .setImageBitmap(
                                bitmap
                            )


                        holder.ivUserProfile.visibility =
                            View.VISIBLE


                        holder.tvUserInitial.visibility =
                            View.GONE

                    } else {

                        // --------------------------------------------
                        // Image unavailable
                        // --------------------------------------------
                        //
                        // Keep the alphabet visible.
                        //
                        // --------------------------------------------

                        holder.ivUserProfile.visibility =
                            View.GONE


                        holder.tvUserInitial.visibility =
                            View.VISIBLE
                    }
                }
            }

        } else {


            // ----------------------------------------------------
            // No profile image in database.
            // ----------------------------------------------------

            holder.ivUserProfile.tag =
                null


            holder.ivUserProfile.visibility =
                View.GONE


            holder.tvUserInitial.visibility =
                View.VISIBLE
        }


        // ========================================================
        // EMAIL
        // ========================================================

        holder.tvUserEmail.text =
            "Email: ${
                user.email
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?: "Not provided"
            }"


        // ========================================================
        // PHONE
        // ========================================================

        holder.tvUserPhone.text =
            "Phone: ${
                user.phone
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?: "Not provided"
            }"


        // ========================================================
        // ADDRESS
        // ========================================================

        holder.tvUserAddress.text =
            "Address: ${
                user.address
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?: "Not provided"
            }"


        // ========================================================
        // ROLE
        // ========================================================

        val role =
            user.role
                ?.uppercase()
                ?: "USER"


        holder.tvUserRole.text =
            role


        // ========================================================
        // STATUS
        // ========================================================

        val status =
            user.status
                ?.uppercase()
                ?: "UNKNOWN"


        holder.tvUserStatus.text =
            status


        // ========================================================
        // USER CARD CLICK
        // ========================================================

        holder.itemView.setOnClickListener {

            onUserClick(
                user
            )
        }


        // ========================================================
        // ADMIN PROTECTION
        // ========================================================

        if (
            role == "ADMIN"
        ) {


            holder.btnDeleteUser.visibility =
                View.GONE


            holder.btnDeleteUser.setOnClickListener(
                null
            )

        } else {


            holder.btnDeleteUser.visibility =
                View.VISIBLE


            holder.btnDeleteUser.setOnClickListener {

                onDeleteClick(
                    user
                )
            }
        }
    }


    // ============================================================
    // BUILD IMAGE URL
    // ============================================================

    private fun buildImageUrl(
        imageUrl: String
    ): String {


        val cleanUrl =
            imageUrl.trim()


        // --------------------------------------------------------
        // Already a complete URL
        // --------------------------------------------------------

        if (
            cleanUrl.startsWith(
                "http://"
            ) ||
            cleanUrl.startsWith(
                "https://"
            )
        ) {

            return cleanUrl
        }


        // --------------------------------------------------------
        // Relative URL
        //
        // Example:
        //
        // /uploads/profile/abc.jpg
        //
        // becomes:
        //
        // http://10.148.238.182:8080/uploads/profile/abc.jpg
        // --------------------------------------------------------

        val baseUrl =
            RetrofitClient
                .IMAGE_BASE_URL
                .trimEnd('/')


        val relativeUrl =
            if (
                cleanUrl.startsWith("/")
            ) {

                cleanUrl

            } else {

                "/$cleanUrl"
            }


        return baseUrl +
                relativeUrl
    }


    // ============================================================
    // DOWNLOAD IMAGE
    // ============================================================

    private fun downloadBitmap(
        imageUrl: String
    ) =
        try {


            val url =
                URL(
                    imageUrl
                )


            val connection =
                url.openConnection()
                        as HttpURLConnection


            connection.connectTimeout =
                10000


            connection.readTimeout =
                10000


            connection.requestMethod =
                "GET"


            connection.doInput =
                true


            connection.connect()


            val responseCode =
                connection.responseCode


            if (
                responseCode in 200..299
            ) {


                connection.inputStream.use {

                    BitmapFactory.decodeStream(
                        it
                    )
                }

            } else {

                null
            }


        } catch (
            e: Exception
        ) {

            null

        } finally {

            // ----------------------------------------------------
            // Connection is closed automatically by disconnect.
            // ----------------------------------------------------

        }


    // ============================================================
    // ITEM COUNT
    // ============================================================

    override fun getItemCount(): Int =
        users.size


    // ============================================================
    // UPDATE LIST
    // ============================================================

    fun updateUsers(
        newUsers:
        List<UserManagementResponse>
    ) {


        users.clear()


        users.addAll(
            newUsers
        )


        notifyDataSetChanged()
    }


    // ============================================================
    // CLEANUP
    // ============================================================

    fun clearImageExecutor() {

        imageExecutor.shutdownNow()
    }
}