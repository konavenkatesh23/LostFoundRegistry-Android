package com.lostfound

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import com.lostfound.model.ClaimRequest
import com.lostfound.network.RetrofitClient
import com.lostfound.session.SessionManager

import kotlinx.coroutines.launch


class CreateClaimActivity : AppCompatActivity() {

    // ============================================================
    // VIEWS
    // ============================================================

    private lateinit var tvItemName: TextView
    private lateinit var tvItemType: TextView

    private lateinit var etClaimMessage: EditText

    private lateinit var btnSubmitClaim: Button
    private lateinit var btnCancel: Button


    // ============================================================
    // SESSION
    // ============================================================

    private lateinit var sessionManager: SessionManager


    // ============================================================
    // ITEM INFORMATION
    // ============================================================

    private var itemId: Long =
        -1L

    private var itemType: String =
        ""

    private var itemName: String =
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
            R.layout.activity_create_claim
        )


        // ========================================================
        // INITIALIZE VIEWS
        // ========================================================

        tvItemName =
            findViewById(
                R.id.tvItemName
            )


        tvItemType =
            findViewById(
                R.id.tvItemType
            )


        etClaimMessage =
            findViewById(
                R.id.etClaimMessage
            )


        btnSubmitClaim =
            findViewById(
                R.id.btnSubmitClaim
            )


        btnCancel =
            findViewById(
                R.id.btnCancel
            )


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
        // DISPLAY
        // ========================================================

        displayItemInformation()


        // ========================================================
        // SUBMIT
        // ========================================================

        btnSubmitClaim.setOnClickListener {

            submitClaim()
        }


        // ========================================================
        // CANCEL
        // ========================================================

        btnCancel.setOnClickListener {

            finish()
        }
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
        // ITEM NAME
        // --------------------------------------------------------

        itemName =
            intent.getStringExtra(
                EXTRA_ITEM_NAME
            ) ?: ""


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
    // DISPLAY ITEM
    // ============================================================

    private fun displayItemInformation() {

        tvItemName.text =
            if (
                itemName.isNotBlank()
            ) {

                itemName

            } else {

                "Item"
            }


        tvItemType.text =
            "Claiming: $itemType"
    }


    // ============================================================
    // SUBMIT CLAIM
    // ============================================================

    private fun submitClaim() {

        val message =
            etClaimMessage
                .text
                .toString()
                .trim()


        // ========================================================
        // VALIDATE
        // ========================================================

        if (
            message.isBlank()
        ) {

            etClaimMessage.error =
                "Please enter a claim message."

            etClaimMessage.requestFocus()

            return
        }


        if (
            message.length < 5
        ) {

            etClaimMessage.error =
                "Please enter at least 5 characters."

            etClaimMessage.requestFocus()

            return
        }


        // ========================================================
        // TOKEN
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
        // AUTHORIZATION
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
        // REQUEST
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
        // DISABLE BUTTON
        // ========================================================

        btnSubmitClaim.isEnabled =
            false

        btnSubmitClaim.text =
            "Submitting..."


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
                        this@CreateClaimActivity,
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
                // ERROR
                // ==================================================

                val errorMessage =
                    response
                        .errorBody()
                        ?.string()
                        ?.trim()


                Toast.makeText(
                    this@CreateClaimActivity,
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
                    this@CreateClaimActivity,
                    e.localizedMessage
                        ?: "Unable to connect to server.",
                    Toast.LENGTH_LONG
                ).show()

            } finally {

                btnSubmitClaim.isEnabled =
                    true

                btnSubmitClaim.text =
                    "Submit Claim"
            }
        }
    }


    // ============================================================
    // CONSTANTS
    // ============================================================

    companion object {

        const val EXTRA_ITEM_ID =
            "item_id"

        const val EXTRA_ITEM_TYPE =
            "item_type"

        const val EXTRA_ITEM_NAME =
            "item_name"
    }
}