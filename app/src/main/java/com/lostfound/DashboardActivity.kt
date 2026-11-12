package com.lostfound

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.lostfound.session.SessionManager

class DashboardActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        sessionManager = SessionManager(this)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)

        val cardLost = findViewById<MaterialCardView>(R.id.cardLost)
        val cardFound = findViewById<MaterialCardView>(R.id.cardFound)
        val cardSearch = findViewById<MaterialCardView>(R.id.cardSearch)
        val cardReports = findViewById<MaterialCardView>(R.id.cardReports)

        val btnProfile = findViewById<Button>(R.id.btnProfile)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        val fullName = intent.getStringExtra("fullName")
            ?: sessionManager.getFullName()
            ?: "User"

        tvWelcome.text = "Welcome, $fullName 👋"

        // Report Lost
        cardLost.setOnClickListener {
            startActivity(
                Intent(this, ReportLostActivity::class.java)
            )
        }

        // Report Found
        cardFound.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ReportFoundActivity::class.java
                )
            )
        }

        // Search Lost Items
        cardSearch.setOnClickListener {
            startActivity(
                Intent(this, LostItemsActivity::class.java)
            )
        }

        // My Reports
        cardReports.setOnClickListener {
            startActivity(
                Intent(this, MyReportsActivity::class.java)
            )
        }

        // Profile
        btnProfile.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }

        // Logout
        btnLogout.setOnClickListener {
            sessionManager.clearSession()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }
    }
}