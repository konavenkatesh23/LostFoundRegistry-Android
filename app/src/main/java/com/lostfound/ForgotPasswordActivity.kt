package com.lostfound

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.lostfound.model.ForgotPasswordRequest
import com.lostfound.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var btnSendOtp: MaterialButton
    private lateinit var tvBackToLogin: android.widget.TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_forgot_password)

        etEmail = findViewById(R.id.etEmail)
        btnSendOtp = findViewById(R.id.btnSendOtp)
        tvBackToLogin = findViewById(R.id.tvBackToLogin)

        btnSendOtp.setOnClickListener {
            sendOtp()
        }

        tvBackToLogin.setOnClickListener {
            finish()
        }
    }

    private fun sendOtp() {

        val email = etEmail.text
            ?.toString()
            ?.trim()
            ?: ""

        // ========================================================
        // Validation
        // ========================================================

        if (email.isEmpty()) {
            etEmail.error = "Enter your email"
            etEmail.requestFocus()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()
        ) {
            etEmail.error = "Enter a valid email"
            etEmail.requestFocus()
            return
        }

        btnSendOtp.isEnabled = false

        val request = ForgotPasswordRequest(
            email = email
        )

        RetrofitClient.authApi
            .forgotPassword(request)
            .enqueue(object : Callback<String> {

                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {

                    btnSendOtp.isEnabled = true

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "OTP sent to your email",
                            Toast.LENGTH_LONG
                        ).show()

                        val intent = Intent(
                            this@ForgotPasswordActivity,
                            OTPVerificationActivity::class.java
                        )

                        intent.putExtra(
                            "email",
                            email
                        )

                        startActivity(intent)

                    } else {

                        val message =
                            response.errorBody()
                                ?.string()
                                ?.trim()
                                ?.takeIf {
                                    it.isNotEmpty()
                                }
                                ?: "Unable to send OTP"

                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {

                    btnSendOtp.isEnabled = true

                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Connection failed: ${
                            t.localizedMessage
                                ?: "Network Error"
                        }",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}