package com.lostfound

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.lostfound.adapter.UserManagementAdapter
import com.lostfound.model.UserManagementResponse
import com.lostfound.network.RetrofitClient
import com.lostfound.session.SessionManager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AdminUserManagementActivity :
    AppCompatActivity() {


    // ============================================================
    // VIEWS
    // ============================================================

    private lateinit var recyclerUsers: RecyclerView

    private lateinit var progressBar: ProgressBar

    private lateinit var tvEmpty: TextView

    private lateinit var tvUserCount: TextView

    private lateinit var btnBack: ImageButton

    private lateinit var btnRefresh: ImageButton


    // ============================================================
    // SESSION
    // ============================================================

    private lateinit var sessionManager: SessionManager


    // ============================================================
    // ADAPTER
    // ============================================================

    private lateinit var userAdapter:
            UserManagementAdapter


    // ============================================================
    // COROUTINE
    // ============================================================

    private var requestJob: Job? = null


    // ============================================================
    // ON CREATE
    // ============================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )


        // ========================================================
        // LAYOUT
        // ========================================================

        setContentView(
            R.layout.activity_admin_user_management
        )


        // ========================================================
        // SESSION
        // ========================================================

        sessionManager =
            SessionManager(this)


        // ========================================================
        // LOGIN CHECK
        // ========================================================

        if (
            !sessionManager.isLoggedIn()
        ) {

            Toast.makeText(
                this,
                "Please login again.",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }


        // ========================================================
        // ADMIN CHECK
        // ========================================================

        if (
            !sessionManager.isAdmin()
        ) {

            Toast.makeText(
                this,
                "Admin access required.",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }


        // ========================================================
        // FIND VIEWS
        // ========================================================

        recyclerUsers =
            findViewById(
                R.id.recyclerUsers
            )

        progressBar =
            findViewById(
                R.id.progressBar
            )

        tvEmpty =
            findViewById(
                R.id.tvEmpty
            )

        tvUserCount =
            findViewById(
                R.id.tvUserCount
            )

        btnBack =
            findViewById(
                R.id.btnBack
            )

        btnRefresh =
            findViewById(
                R.id.btnRefresh
            )


        // ========================================================
        // RECYCLER VIEW
        // ========================================================

        recyclerUsers.layoutManager =
            LinearLayoutManager(this)

        recyclerUsers.setHasFixedSize(false)


        // ========================================================
        // USER ADAPTER
        // ========================================================

        userAdapter =
            UserManagementAdapter(

                mutableListOf(),

                // ------------------------------------------------
                // USER CARD CLICK
                // ------------------------------------------------

                onUserClick = { user ->

                    openUserReports(
                        user
                    )
                },

                // ------------------------------------------------
                // DELETE USER CLICK
                // ------------------------------------------------

                onDeleteClick = { user ->

                    showDeleteConfirmation(
                        user
                    )
                }
            )


        recyclerUsers.adapter =
            userAdapter


        // ========================================================
        // BACK BUTTON
        // ========================================================

        btnBack.setOnClickListener {

            finish()
        }


        // ========================================================
        // REFRESH BUTTON
        // ========================================================

        btnRefresh.setOnClickListener {

            loadUsers()
        }


        // ========================================================
        // LOAD USERS
        // ========================================================

        loadUsers()
    }


    // ============================================================
    // OPEN USER REPORTS
    // ============================================================

    private fun openUserReports(
        user: UserManagementResponse
    ) {

        val intent =
            Intent(
                this,
                AdminUserReportsActivity::class.java
            )


        intent.putExtra(
            AdminUserReportsActivity.EXTRA_USER_ID,
            user.id
        )


        intent.putExtra(
            AdminUserReportsActivity.EXTRA_USER_NAME,
            user.fullName
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: "Unknown User"
        )


        intent.putExtra(
            AdminUserReportsActivity.EXTRA_USER_EMAIL,
            user.email
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: "No email"
        )


        startActivity(
            intent
        )
    }


    // ============================================================
    // LOAD USERS
    // ============================================================

    private fun loadUsers() {

        val token =
            sessionManager.getToken()


        if (
            token.isNullOrBlank()
        ) {

            Toast.makeText(
                this,
                "Session expired. Please login again.",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }


        // ========================================================
        // SHOW LOADING
        // ========================================================

        progressBar.visibility =
            View.VISIBLE

        tvEmpty.visibility =
            View.GONE


        requestJob?.cancel()


        requestJob =
            CoroutineScope(
                Dispatchers.IO
            ).launch {

                try {

                    val response =
                        RetrofitClient
                            .userApi
                            .getAllUsers(
                                "Bearer $token"
                            )


                    withContext(
                        Dispatchers.Main
                    ) {

                        progressBar.visibility =
                            View.GONE


                        if (
                            response.isSuccessful
                        ) {

                            val users =
                                response.body()
                                    ?: emptyList()


                            userAdapter.updateUsers(
                                users
                            )


                            tvUserCount.text =
                                if (
                                    users.size == 1
                                ) {

                                    "1 user"

                                } else {

                                    "${users.size} users"
                                }


                            tvEmpty.visibility =
                                if (
                                    users.isEmpty()
                                ) {

                                    View.VISIBLE

                                } else {

                                    View.GONE
                                }


                        } else {

                            handleApiError(
                                response.code()
                            )
                        }
                    }

                } catch (
                    e: Exception
                ) {

                    withContext(
                        Dispatchers.Main
                    ) {

                        progressBar.visibility =
                            View.GONE


                        Toast.makeText(
                            this@AdminUserManagementActivity,
                            e.localizedMessage
                                ?: "Unable to connect to server.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }


    // ============================================================
    // DELETE CONFIRMATION
    // ============================================================

    private fun showDeleteConfirmation(
        user: UserManagementResponse
    ) {

        val userName =
            user.fullName
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: "this user"


        AlertDialog.Builder(this)

            .setTitle(
                "Delete User?"
            )

            .setMessage(
                "Are you sure you want to delete $userName?\n\n" +
                        "This will permanently remove the user's " +
                        "account and associated reports and claims."
            )

            .setNegativeButton(
                "CANCEL",
                null
            )

            .setPositiveButton(
                "DELETE"
            ) { _, _ ->

                deleteUser(
                    user
                )
            }

            .show()
    }


    // ============================================================
    // DELETE USER
    // ============================================================

    private fun deleteUser(
        user: UserManagementResponse
    ) {

        val token =
            sessionManager.getToken()


        if (
            token.isNullOrBlank()
        ) {

            Toast.makeText(
                this,
                "Session expired. Please login again.",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }


        progressBar.visibility =
            View.VISIBLE


        requestJob?.cancel()


        requestJob =
            CoroutineScope(
                Dispatchers.IO
            ).launch {

                try {

                    val response =
                        RetrofitClient
                            .userApi
                            .deleteUser(
                                "Bearer $token",
                                user.id
                            )


                    withContext(
                        Dispatchers.Main
                    ) {

                        progressBar.visibility =
                            View.GONE


                        if (
                            response.isSuccessful
                        ) {

                            Toast.makeText(
                                this@AdminUserManagementActivity,
                                "User deleted successfully.",
                                Toast.LENGTH_SHORT
                            ).show()


                            loadUsers()

                        } else {

                            handleApiError(
                                response.code()
                            )
                        }
                    }

                } catch (
                    e: Exception
                ) {

                    withContext(
                        Dispatchers.Main
                    ) {

                        progressBar.visibility =
                            View.GONE


                        Toast.makeText(
                            this@AdminUserManagementActivity,
                            e.localizedMessage
                                ?: "Unable to delete user.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }


    // ============================================================
    // API ERROR
    // ============================================================

    private fun handleApiError(
        statusCode: Int
    ) {

        when (
            statusCode
        ) {

            400 -> {

                Toast.makeText(
                    this,
                    "Invalid request.",
                    Toast.LENGTH_LONG
                ).show()
            }


            401 -> {

                Toast.makeText(
                    this,
                    "Session expired. Please login again.",
                    Toast.LENGTH_LONG
                ).show()
            }


            403 -> {

                Toast.makeText(
                    this,
                    "Admin access required.",
                    Toast.LENGTH_LONG
                ).show()
            }


            404 -> {

                Toast.makeText(
                    this,
                    "User not found.",
                    Toast.LENGTH_LONG
                ).show()
            }


            else -> {

                Toast.makeText(
                    this,
                    "Server error: $statusCode",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    // ============================================================
    // ON RESUME
    // ============================================================

    override fun onResume() {

        super.onResume()

        if (
            ::sessionManager.isInitialized &&
            sessionManager.isLoggedIn() &&
            sessionManager.isAdmin()
        ) {

            loadUsers()
        }
    }


    // ============================================================
    // ON DESTROY
    // ============================================================

    override fun onDestroy() {

        requestJob?.cancel()

        super.onDestroy()
    }
}