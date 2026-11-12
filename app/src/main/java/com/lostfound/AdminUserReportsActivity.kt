package com.lostfound

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.lostfound.adapter.AdminReportAdapter
import com.lostfound.model.FoundItem
import com.lostfound.model.LostItem
import com.lostfound.network.RetrofitClient
import com.lostfound.session.SessionManager

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AdminUserReportsActivity :
    AppCompatActivity() {


    // ============================================================
    // EXTRAS
    // ============================================================

    companion object {

        const val EXTRA_USER_ID =
            "extra_user_id"

        const val EXTRA_USER_NAME =
            "extra_user_name"

        const val EXTRA_USER_EMAIL =
            "extra_user_email"
    }


    // ============================================================
    // VIEWS
    // ============================================================

    private lateinit var btnBack: ImageButton

    private lateinit var tvUserName: TextView

    private lateinit var tvUserEmail: TextView

    private lateinit var tvLostCount: TextView

    private lateinit var tvFoundCount: TextView

    private lateinit var tvLostEmpty: TextView

    private lateinit var tvFoundEmpty: TextView

    private lateinit var recyclerLost: RecyclerView

    private lateinit var recyclerFound: RecyclerView

    private lateinit var progressBar: ProgressBar


    // ============================================================
    // SESSION
    // ============================================================

    private lateinit var sessionManager: SessionManager


    // ============================================================
    // USER
    // ============================================================

    private var userId: Long =
        -1L

    private var userName: String =
        "Unknown User"

    private var userEmail: String =
        "No email"


    // ============================================================
    // ADAPTERS
    // ============================================================

    private lateinit var lostAdapter:
            AdminReportAdapter<LostItem>

    private lateinit var foundAdapter:
            AdminReportAdapter<FoundItem>


    // ============================================================
    // REQUEST COUNTER
    // ============================================================

    private var completedRequests =
        0


    // ============================================================
    // ON CREATE
    // ============================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )


        setContentView(
            R.layout.activity_admin_user_reports
        )


        // ========================================================
        // SESSION
        // ========================================================

        sessionManager =
            SessionManager(this)


        if (
            !sessionManager.isLoggedIn()
        ) {

            finish()

            return
        }


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
        // USER INFORMATION
        // ========================================================

        userId =
            intent.getLongExtra(
                EXTRA_USER_ID,
                -1L
            )


        userName =
            intent.getStringExtra(
                EXTRA_USER_NAME
            )
                ?: "Unknown User"


        userEmail =
            intent.getStringExtra(
                EXTRA_USER_EMAIL
            )
                ?: "No email"


        if (
            userId <= 0L
        ) {

            Toast.makeText(
                this,
                "Invalid user.",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }


        // ========================================================
        // FIND VIEWS
        // ========================================================

        btnBack =
            findViewById(
                R.id.btnBack
            )


        tvUserName =
            findViewById(
                R.id.tvUserName
            )


        tvUserEmail =
            findViewById(
                R.id.tvUserEmail
            )


        tvLostCount =
            findViewById(
                R.id.tvLostCount
            )


        tvFoundCount =
            findViewById(
                R.id.tvFoundCount
            )


        tvLostEmpty =
            findViewById(
                R.id.tvLostEmpty
            )


        tvFoundEmpty =
            findViewById(
                R.id.tvFoundEmpty
            )


        recyclerLost =
            findViewById(
                R.id.recyclerLost
            )


        recyclerFound =
            findViewById(
                R.id.recyclerFound
            )


        progressBar =
            findViewById(
                R.id.progressBar
            )


        // ========================================================
        // USER INFORMATION
        // ========================================================

        tvUserName.text =
            userName


        tvUserEmail.text =
            userEmail


        // ========================================================
        // RECYCLER VIEWS
        // ========================================================

        recyclerLost.layoutManager =
            LinearLayoutManager(this)


        recyclerFound.layoutManager =
            LinearLayoutManager(this)


        recyclerLost.setHasFixedSize(false)

        recyclerFound.setHasFixedSize(false)


        // ========================================================
        // LOST ADAPTER
        // ========================================================

        lostAdapter =
            AdminReportAdapter<LostItem>(
                mutableListOf(),
                isLost = true
            ) { item ->

                showDeleteLostConfirmation(
                    item
                )
            }


        recyclerLost.adapter =
            lostAdapter


        // ========================================================
        // FOUND ADAPTER
        // ========================================================

        foundAdapter =
            AdminReportAdapter<FoundItem>(
                mutableListOf(),
                isLost = false
            ) { item ->

                showDeleteFoundConfirmation(
                    item
                )
            }


        recyclerFound.adapter =
            foundAdapter


        // ========================================================
        // BACK
        // ========================================================

        btnBack.setOnClickListener {

            finish()
        }


        // ========================================================
        // LOAD
        // ========================================================

        loadUserReports()
    }


    // ============================================================
    // LOAD USER REPORTS
    // ============================================================

    private fun loadUserReports() {

        val tokenValue =
            sessionManager.getToken()


        if (
            tokenValue.isNullOrBlank()
        ) {

            Toast.makeText(
                this,
                "Session expired. Please login again.",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }


        val authorization =
            if (
                tokenValue.startsWith(
                    "Bearer ",
                    ignoreCase = true
                )
            ) {

                tokenValue

            } else {

                "Bearer $tokenValue"
            }


        completedRequests = 0


        progressBar.visibility =
            View.VISIBLE


        // ========================================================
        // LOST REPORTS
        // ========================================================

        RetrofitClient.lostItemApi
            .getAdminLostItemsByUser(
                authorization,
                userId
            )
            .enqueue(
                object :
                    Callback<List<LostItem>> {

                    override fun onResponse(
                        call: Call<List<LostItem>>,
                        response: Response<List<LostItem>>
                    ) {

                        if (
                            response.isSuccessful
                        ) {

                            val items =
                                response.body()
                                    ?: emptyList()


                            lostAdapter.updateItems(
                                items
                            )


                            tvLostCount.text =
                                "${items.size} Reports"


                            tvLostEmpty.visibility =
                                if (
                                    items.isEmpty()
                                ) {

                                    View.VISIBLE

                                } else {

                                    View.GONE
                                }

                        } else {

                            Toast.makeText(
                                this@AdminUserReportsActivity,
                                "Failed to load lost reports.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                        checkLoadingFinished()
                    }


                    override fun onFailure(
                        call: Call<List<LostItem>>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            this@AdminUserReportsActivity,
                            "Unable to load lost reports.",
                            Toast.LENGTH_SHORT
                        ).show()


                        checkLoadingFinished()
                    }
                }
            )


        // ========================================================
        // FOUND REPORTS
        // ========================================================

        RetrofitClient.foundItemApi
            .getAdminFoundItemsByUser(
                authorization,
                userId
            )
            .enqueue(
                object :
                    Callback<List<FoundItem>> {

                    override fun onResponse(
                        call: Call<List<FoundItem>>,
                        response: Response<List<FoundItem>>
                    ) {

                        if (
                            response.isSuccessful
                        ) {

                            val items =
                                response.body()
                                    ?: emptyList()


                            foundAdapter.updateItems(
                                items
                            )


                            tvFoundCount.text =
                                "${items.size} Reports"


                            tvFoundEmpty.visibility =
                                if (
                                    items.isEmpty()
                                ) {

                                    View.VISIBLE

                                } else {

                                    View.GONE
                                }

                        } else {

                            Toast.makeText(
                                this@AdminUserReportsActivity,
                                "Failed to load found reports.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                        checkLoadingFinished()
                    }


                    override fun onFailure(
                        call: Call<List<FoundItem>>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            this@AdminUserReportsActivity,
                            "Unable to load found reports.",
                            Toast.LENGTH_SHORT
                        ).show()


                        checkLoadingFinished()
                    }
                }
            )
    }


    // ============================================================
    // CHECK LOADING
    // ============================================================

    private fun checkLoadingFinished() {

        completedRequests++


        if (
            completedRequests >= 2
        ) {

            progressBar.visibility =
                View.GONE
        }
    }


    // ============================================================
    // DELETE LOST CONFIRMATION
    // ============================================================

    private fun showDeleteLostConfirmation(
        item: LostItem
    ) {

        AlertDialog.Builder(this)

            .setTitle(
                "Delete Lost Report?"
            )

            .setMessage(
                "Delete \"${item.itemName}\" from this user's lost reports?"
            )

            .setNegativeButton(
                "CANCEL",
                null
            )

            .setPositiveButton(
                "DELETE"
            ) { _, _ ->

                deleteLostItem(
                    item
                )
            }

            .show()
    }


    // ============================================================
    // DELETE FOUND CONFIRMATION
    // ============================================================

    private fun showDeleteFoundConfirmation(
        item: FoundItem
    ) {

        AlertDialog.Builder(this)

            .setTitle(
                "Delete Found Report?"
            )

            .setMessage(
                "Delete \"${item.itemName}\" from this user's found reports?"
            )

            .setNegativeButton(
                "CANCEL",
                null
            )

            .setPositiveButton(
                "DELETE"
            ) { _, _ ->

                deleteFoundItem(
                    item
                )
            }

            .show()
    }


    // ============================================================
    // DELETE LOST REPORT
    // ============================================================

    private fun deleteLostItem(
        item: LostItem
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

            return
        }


        val authorization =
            if (
                token.startsWith(
                    "Bearer ",
                    ignoreCase = true
                )
            ) {

                token

            } else {

                "Bearer $token"
            }


        progressBar.visibility =
            View.VISIBLE


        RetrofitClient.lostItemApi
            .deleteAdminLostItem(
                authorization,
                userId,
                item.id
            )
            .enqueue(
                object :
                    Callback<String> {

                    override fun onResponse(
                        call: Call<String>,
                        response: Response<String>
                    ) {

                        progressBar.visibility =
                            View.GONE


                        if (
                            response.isSuccessful
                        ) {

                            Toast.makeText(
                                this@AdminUserReportsActivity,
                                "Lost report deleted.",
                                Toast.LENGTH_SHORT
                            ).show()


                            loadUserReports()

                        } else {

                            Toast.makeText(
                                this@AdminUserReportsActivity,
                                "Unable to delete lost report.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }


                    override fun onFailure(
                        call: Call<String>,
                        t: Throwable
                    ) {

                        progressBar.visibility =
                            View.GONE


                        Toast.makeText(
                            this@AdminUserReportsActivity,
                            "Server connection failed.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }


    // ============================================================
    // DELETE FOUND REPORT
    // ============================================================

    private fun deleteFoundItem(
        item: FoundItem
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

            return
        }


        val authorization =
            if (
                token.startsWith(
                    "Bearer ",
                    ignoreCase = true
                )
            ) {

                token

            } else {

                "Bearer $token"
            }


        progressBar.visibility =
            View.VISIBLE


        RetrofitClient.foundItemApi
            .deleteAdminFoundItem(
                authorization,
                userId,
                item.id
            )
            .enqueue(
                object :
                    Callback<String> {

                    override fun onResponse(
                        call: Call<String>,
                        response: Response<String>
                    ) {

                        progressBar.visibility =
                            View.GONE


                        if (
                            response.isSuccessful
                        ) {

                            Toast.makeText(
                                this@AdminUserReportsActivity,
                                "Found report deleted.",
                                Toast.LENGTH_SHORT
                            ).show()


                            loadUserReports()

                        } else {

                            Toast.makeText(
                                this@AdminUserReportsActivity,
                                "Unable to delete found report.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }


                    override fun onFailure(
                        call: Call<String>,
                        t: Throwable
                    ) {

                        progressBar.visibility =
                            View.GONE


                        Toast.makeText(
                            this@AdminUserReportsActivity,
                            "Server connection failed.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }
}