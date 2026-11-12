package com.lostfound

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.google.android.material.button.MaterialButton
import com.lostfound.session.SessionManager


class AdminDashboardActivity : AppCompatActivity() {


    // ============================================================
    // VIEWS
    // ============================================================

    private lateinit var tvWelcome: TextView

    private lateinit var btnPendingClaims: MaterialButton

    private lateinit var btnUserManagement: MaterialButton

    private lateinit var btnLogout: MaterialButton


    // ============================================================
    // SESSION
    // ============================================================

    private lateinit var sessionManager: SessionManager


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
            R.layout.activity_admin_dashboard
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

            openLogin()

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

            openUserDashboard()

            return
        }


        // ========================================================
        // VIEWS
        // ========================================================

        tvWelcome =
            findViewById(
                R.id.tvAdminWelcome
            )


        btnPendingClaims =
            findViewById(
                R.id.btnPendingClaims
            )


        btnUserManagement =
            findViewById(
                R.id.btnUserManagement
            )


        btnLogout =
            findViewById(
                R.id.btnAdminLogout
            )


        // ========================================================
        // WELCOME
        // ========================================================

        val fullName =
            sessionManager.getFullName()


        tvWelcome.text =
            if (
                !fullName.isNullOrBlank()
            ) {

                "Welcome, $fullName"

            } else {

                "Welcome, Administrator"
            }


        // ========================================================
        // PENDING CLAIMS
        // ========================================================

        btnPendingClaims.setOnClickListener {

            try {

                val intent =
                    Intent(
                        this,
                        AdminClaimsActivity::class.java
                    )

                startActivity(intent)

            } catch (
                e: Exception
            ) {

                Toast.makeText(
                    this,
                    "Unable to open Claims Management.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        // ========================================================
        // USER MANAGEMENT
        // ========================================================

        btnUserManagement.setOnClickListener {

            val intent =
                Intent(
                    this,
                    AdminUserManagementActivity::class.java
                )

            startActivity(intent)
        }


        // ========================================================
        // LOGOUT
        // ========================================================

        btnLogout.setOnClickListener {

            logoutAdmin()
        }
    }


    // ============================================================
    // LOGOUT
    // ============================================================

    private fun logoutAdmin() {

        sessionManager.clearSession()


        Toast.makeText(
            this,
            "Logged out successfully.",
            Toast.LENGTH_SHORT
        ).show()


        openLogin()
    }


    // ============================================================
    // OPEN LOGIN
    // ============================================================

    private fun openLogin() {

        val intent =
            Intent(
                this,
                LoginActivity::class.java
            )


        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK


        startActivity(intent)

        finish()
    }


    // ============================================================
    // OPEN USER DASHBOARD
    // ============================================================

    private fun openUserDashboard() {

        val intent =
            Intent(
                this,
                DashboardActivity::class.java
            )


        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK


        startActivity(intent)

        finish()
    }
}