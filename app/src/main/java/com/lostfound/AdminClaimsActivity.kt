package com.lostfound

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.appbar.MaterialToolbar
import com.lostfound.adapter.AdminClaimAdapter
import com.lostfound.model.ClaimResponse
import com.lostfound.network.RetrofitClient
import com.lostfound.session.SessionManager

import kotlinx.coroutines.launch


class AdminClaimsActivity : AppCompatActivity() {


    // ============================================================
    // VIEWS
    // ============================================================

    private lateinit var toolbar: MaterialToolbar

    private lateinit var recyclerView: RecyclerView

    private lateinit var progressBar: ProgressBar

    private lateinit var tvNoPendingClaims: TextView

    private lateinit var tvPendingClaimCount: TextView


    // ============================================================
    // SESSION
    // ============================================================

    private lateinit var sessionManager: SessionManager


    // ============================================================
    // ADAPTER
    // ============================================================

    private lateinit var claimsAdapter: AdminClaimAdapter


    // ============================================================
    // DATA
    // ============================================================

    private val claims =
        mutableListOf<ClaimResponse>()


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
            R.layout.activity_admin_claims
        )


        // ========================================================
        // SESSION
        // ========================================================

        sessionManager =
            SessionManager(this)


        // ========================================================
        // SECURITY CHECK
        // ========================================================

        if (
            !sessionManager.isLoggedIn()
        ) {

            Toast.makeText(
                this,
                "Please login first.",
                Toast.LENGTH_LONG
            ).show()

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
        // INITIALIZE VIEWS
        // ========================================================

        initializeViews()


        // ========================================================
        // INITIALIZE RECYCLER
        // ========================================================

        initializeRecyclerView()


        // ========================================================
        // BACK BUTTON
        // ========================================================

        toolbar.setNavigationOnClickListener {

            finish()
        }


        // ========================================================
        // LOAD CLAIMS
        // ========================================================

        loadPendingClaims()
    }


    // ============================================================
    // INITIALIZE VIEWS
    // ============================================================

    private fun initializeViews() {

        toolbar =
            findViewById(
                R.id.toolbarAdminClaims
            )


        recyclerView =
            findViewById(
                R.id.recyclerAdminClaims
            )


        progressBar =
            findViewById(
                R.id.progressAdminClaims
            )


        tvNoPendingClaims =
            findViewById(
                R.id.tvNoPendingClaims
            )


        tvPendingClaimCount =
            findViewById(
                R.id.tvPendingClaimCount
            )
    }


    // ============================================================
    // INITIALIZE RECYCLER VIEW
    // ============================================================

    private fun initializeRecyclerView() {

        recyclerView.layoutManager =
            LinearLayoutManager(
                this
            )


        recyclerView.setHasFixedSize(
            false
        )


        claimsAdapter =
            AdminClaimAdapter(

                this,

                claims,

                onApproveClick = {
                        claim ->
                    showApproveConfirmation(
                        claim
                    )
                },

                onRejectClick = {
                        claim ->
                    showRejectConfirmation(
                        claim
                    )
                }
            )


        recyclerView.adapter =
            claimsAdapter
    }


    // ============================================================
    // LOAD PENDING CLAIMS
    // ============================================================

    private fun loadPendingClaims() {

        val tokenValue =
            sessionManager.getToken()


        if (
            tokenValue.isNullOrBlank()
        ) {

            Toast.makeText(
                this,
                "Your session has expired. Please login again.",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }


        val authorizationToken =
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


        setLoading(
            true
        )


        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient
                        .claimApi
                        .getPendingClaims(
                            authorizationToken
                        )


                if (
                    response.isSuccessful
                ) {

                    val data =
                        response.body()
                            ?: emptyList()


                    claimsAdapter.updateClaims(
                        data
                    )


                    updateEmptyState(
                        data
                    )


                } else {

                    handleServerError(
                        response.code()
                    )
                }


            } catch (e: Exception) {

                Toast.makeText(
                    this@AdminClaimsActivity,
                    "Unable to load pending claims: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

            } finally {

                setLoading(
                    false
                )
            }
        }
    }


    // ============================================================
    // APPROVE CONFIRMATION
    // ============================================================

    private fun showApproveConfirmation(
        claim: ClaimResponse
    ) {

        AlertDialog.Builder(
            this
        )
            .setTitle(
                "Approve Claim"
            )
            .setMessage(
                "Are you sure you want to approve Claim #${claim.id} for ${claim.itemName}?"
            )
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Approve"
            ) { _, _ ->

                approveClaim(
                    claim
                )
            }
            .show()
    }


    // ============================================================
    // REJECT CONFIRMATION
    // ============================================================

    private fun showRejectConfirmation(
        claim: ClaimResponse
    ) {

        AlertDialog.Builder(
            this
        )
            .setTitle(
                "Reject Claim"
            )
            .setMessage(
                "Are you sure you want to reject Claim #${claim.id} for ${claim.itemName}?"
            )
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Reject"
            ) { _, _ ->

                rejectClaim(
                    claim
                )
            }
            .show()
    }


    // ============================================================
    // APPROVE CLAIM
    // ============================================================

    private fun approveClaim(
        claim: ClaimResponse
    ) {

        val tokenValue =
            sessionManager.getToken()


        if (
            tokenValue.isNullOrBlank()
        ) {

            Toast.makeText(
                this,
                "Your session has expired. Please login again.",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }


        val authorizationToken =
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


        setLoading(
            true
        )


        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient
                        .claimApi
                        .approveClaim(
                            authorizationToken,
                            claim.id
                        )


                if (
                    response.isSuccessful
                ) {

                    Toast.makeText(
                        this@AdminClaimsActivity,
                        "Claim approved successfully.",
                        Toast.LENGTH_LONG
                    ).show()


                    loadPendingClaims()

                } else {

                    handleActionError(
                        response.code(),
                        "approve"
                    )
                }


            } catch (e: Exception) {

                Toast.makeText(
                    this@AdminClaimsActivity,
                    "Approval failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

            } finally {

                setLoading(
                    false
                )
            }
        }
    }


    // ============================================================
    // REJECT CLAIM
    // ============================================================

    private fun rejectClaim(
        claim: ClaimResponse
    ) {

        val tokenValue =
            sessionManager.getToken()


        if (
            tokenValue.isNullOrBlank()
        ) {

            Toast.makeText(
                this,
                "Your session has expired. Please login again.",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }


        val authorizationToken =
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


        setLoading(
            true
        )


        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient
                        .claimApi
                        .rejectClaim(
                            authorizationToken,
                            claim.id
                        )


                if (
                    response.isSuccessful
                ) {

                    Toast.makeText(
                        this@AdminClaimsActivity,
                        "Claim rejected successfully.",
                        Toast.LENGTH_LONG
                    ).show()


                    loadPendingClaims()

                } else {

                    handleActionError(
                        response.code(),
                        "reject"
                    )
                }


            } catch (e: Exception) {

                Toast.makeText(
                    this@AdminClaimsActivity,
                    "Rejection failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

            } finally {

                setLoading(
                    false
                )
            }
        }
    }


    // ============================================================
    // UPDATE EMPTY STATE
    // ============================================================

    private fun updateEmptyState(
        data: List<ClaimResponse>
    ) {

        tvPendingClaimCount.text =
            "Pending claims: ${data.size}"


        if (
            data.isEmpty()
        ) {

            tvNoPendingClaims.visibility =
                View.VISIBLE

            recyclerView.visibility =
                View.GONE

        } else {

            tvNoPendingClaims.visibility =
                View.GONE

            recyclerView.visibility =
                View.VISIBLE
        }
    }


    // ============================================================
    // LOADING
    // ============================================================

    private fun setLoading(
        loading: Boolean
    ) {

        progressBar.visibility =
            if (loading) {

                View.VISIBLE

            } else {

                View.GONE
            }


        if (loading) {

            recyclerView.alpha =
                0.5f

        } else {

            recyclerView.alpha =
                1.0f
        }
    }


    // ============================================================
    // SERVER ERROR
    // ============================================================

    private fun handleServerError(
        statusCode: Int
    ) {

        val message =
            when (statusCode) {

                401 ->
                    "Session expired. Please login again."

                403 ->
                    "Admin access required."

                404 ->
                    "Pending claims endpoint not found."

                else ->
                    "Server error. Code: $statusCode"
            }


        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()


        if (
            statusCode == 401 ||
            statusCode == 403
        ) {

            finish()
        }
    }


    // ============================================================
    // ACTION ERROR
    // ============================================================

    private fun handleActionError(
        statusCode: Int,
        action: String
    ) {

        val message =
            when (statusCode) {

                401 ->
                    "Session expired. Please login again."

                403 ->
                    "Only administrators can $action claims."

                404 ->
                    "Claim not found."

                else ->
                    "Unable to $action claim. Error: $statusCode"
            }


        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()


        if (
            statusCode == 401 ||
            statusCode == 403
        ) {

            finish()
        }
    }


    // ============================================================
    // REFRESH WHEN RETURNING
    // ============================================================

    override fun onResume() {

        super.onResume()


        if (
            ::sessionManager.isInitialized &&
            sessionManager.isAdmin()
        ) {

            loadPendingClaims()
        }
    }
}