package com.lostfound

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

import com.lostfound.model.ClaimRequest
import com.lostfound.network.RetrofitClient
import com.lostfound.session.SessionManager

import kotlinx.coroutines.launch


class ClaimActivity : AppCompatActivity() {

    // ============================================================
    // VIEWS
    // ============================================================

    private lateinit var tvClaimItemType: TextView
    private lateinit var tvClaimItemName: TextView

    private lateinit var tilClaimMessage: TextInputLayout
    private lateinit var etClaimMessage: TextInputEditText

    private lateinit var btnSubmitClaim: MaterialButton
    private lateinit var progressClaim: ProgressBar


    // ============================================================
    // SESSION
    // ============================================================

    private lateinit var sessionManager: SessionManager


    // ============================================================
    // ITEM INFORMATION
    // ============================================================

    private var itemId: Long =
        -1L

    private var itemName: String =
        ""

    private var itemType: String =
        ""


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
            R.layout.activity_claim
        )


        // ========================================================
        // INITIALIZE VIEWS
        // ========================================================

        initializeViews()


        // ========================================================
        // SESSION
        // ========================================================

        sessionManager =
            SessionManager(
                this
            )


        // ========================================================
        // READ INTENT
        // ========================================================

        if (
            !readIntentData()
        ) {

            return
        }


        // ========================================================
        // DISPLAY ITEM
        // ========================================================

        displayItemInformation()


        // ========================================================
        // SUBMIT
        // ========================================================

        btnSubmitClaim.setOnClickListener {

            submitClaim()
        }
    }


    // ============================================================
    // INITIALIZE VIEWS
    // ============================================================

    private fun initializeViews() {

        tvClaimItemType =
            findViewById(
                R.id.tvClaimItemType
            )


        tvClaimItemName =
            findViewById(
                R.id.tvClaimItemName
            )


        tilClaimMessage =
            findViewById(
                R.id.tilClaimMessage
            )


        etClaimMessage =
            findViewById(
                R.id.etClaimMessage
            )


        btnSubmitClaim =
            findViewById(
                R.id.btnSubmitClaim
            )


        progressClaim =
            findViewById(
                R.id.progressClaim
            )
    }


    // ============================================================
    // READ INTENT DATA
    // ============================================================

    private fun readIntentData(): Boolean {

        // --------------------------------------------------------
        // ITEM ID
        // --------------------------------------------------------

        itemId =
            intent.getLongExtra(
                EXTRA_ITEM_ID,
                -1L
            )


        // --------------------------------------------------------
        // ITEM NAME
        // --------------------------------------------------------

        itemName =
            intent.getStringExtra(
                EXTRA_ITEM_NAME
            ) ?: ""


        // --------------------------------------------------------
        // ITEM TYPE
        // --------------------------------------------------------

        itemType =
            intent.getStringExtra(
                EXTRA_ITEM_TYPE
            )
                ?.trim()
                ?.uppercase()
                ?: ""


        // --------------------------------------------------------
        // VALIDATE ITEM ID
        // --------------------------------------------------------

        if (
            itemId <= 0L
        ) {

            Toast.makeText(
                this,
                "Invalid item.",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return false
        }


        // --------------------------------------------------------
        // VALIDATE ITEM TYPE
        // --------------------------------------------------------

        if (
            itemType != "LOST" &&
            itemType != "FOUND"
        ) {

            Toast.makeText(
                this,
                "Invalid item type.",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return false
        }


        return true
    }


    // ============================================================
    // DISPLAY ITEM INFORMATION
    // ============================================================

    private fun displayItemInformation() {

        tvClaimItemName.text =
            if (
                itemName.isBlank()
            ) {

                "Item"

            } else {

                itemName
            }


        tvClaimItemType.text =
            if (
                itemType == "LOST"
            ) {

                "LOST ITEM"

            } else {

                "FOUND ITEM"
            }
    }


    // ============================================================
    // SUBMIT CLAIM
    // ============================================================

    private fun submitClaim() {

        // --------------------------------------------------------
        // MESSAGE
        // --------------------------------------------------------

        val message =
            etClaimMessage
                .text
                ?.toString()
                ?.trim()
                ?: ""


        // --------------------------------------------------------
        // VALIDATE MESSAGE
        // --------------------------------------------------------

        if (
            message.isBlank()
        ) {

            tilClaimMessage.error =
                "Please enter a claim message."

            etClaimMessage.requestFocus()

            return
        }


        // --------------------------------------------------------
        // MINIMUM LENGTH
        // --------------------------------------------------------

        if (
            message.length < 10
        ) {

            tilClaimMessage.error =
                "Please provide more details."

            etClaimMessage.requestFocus()

            return
        }


        tilClaimMessage.error =
            null


        // ========================================================
        // GET TOKEN
        // ========================================================

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

            return
        }


        // ========================================================
        // AUTHORIZATION TOKEN
        // ========================================================

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


        // ========================================================
        // CREATE REQUEST
        // ========================================================
        //
        // IMPORTANT:
        //
        // Current backend expects:
        //
        // itemId
        // itemType
        // claimMessage
        //
        // ========================================================

        val request =
            ClaimRequest(

                itemId =
                    itemId,

                itemType =
                    itemType,

                claimMessage =
                    message
            )


        // ========================================================
        // LOADING
        // ========================================================

        setLoading(
            true
        )


        // ========================================================
        // API CALL
        // ========================================================

        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient
                        .claimApi
                        .createClaim(
                            authorizationToken,
                            request
                        )


                // ==================================================
                // SUCCESS
                // ==================================================

                if (
                    response.isSuccessful
                ) {

                    Toast.makeText(
                        this@ClaimActivity,
                        "Claim submitted successfully.",
                        Toast.LENGTH_LONG
                    ).show()


                    setResult(
                        RESULT_OK
                    )


                    finish()

                    return@launch
                }


                // ==================================================
                // SERVER ERROR
                // ==================================================

                val errorMessage =
                    response
                        .errorBody()
                        ?.string()
                        ?.trim()


                Toast.makeText(
                    this@ClaimActivity,
                    if (
                        !errorMessage.isNullOrBlank()
                    ) {

                        errorMessage

                    } else {

                        "Failed to submit claim."
                    },
                    Toast.LENGTH_LONG
                ).show()

            } catch (
                e: Exception
            ) {

                Toast.makeText(
                    this@ClaimActivity,
                    e.localizedMessage
                        ?: "Unable to connect to server.",
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
    // LOADING
    // ============================================================

    private fun setLoading(
        loading: Boolean
    ) {

        progressClaim.visibility =
            if (
                loading
            ) {

                View.VISIBLE

            } else {

                View.GONE
            }


        btnSubmitClaim.isEnabled =
            !loading


        etClaimMessage.isEnabled =
            !loading
    }


    // ============================================================
    // INTENT CONSTANTS
    // ============================================================

    companion object {

        const val EXTRA_ITEM_ID =
            "item_id"

        const val EXTRA_ITEM_NAME =
            "item_name"

        const val EXTRA_ITEM_TYPE =
            "item_type"
    }
}