package com.lostfound

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.lostfound.model.VerifyOtpRequest
import com.lostfound.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OTPVerificationActivity : AppCompatActivity() {

    private lateinit var etOtp: TextInputEditText
    private lateinit var btnVerifyOtp: MaterialButton

    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_otp_verification)

        etOtp = findViewById(R.id.etOtp)
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp)

        email = intent.getStringExtra("email") ?: ""

        if (email.isEmpty()) {
            Toast.makeText(
                this,
                "Email not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        btnVerifyOtp.setOnClickListener {
            verifyOtp()
        }
    }

    private fun verifyOtp() {

        val otp = etOtp.text
            ?.toString()
            ?.trim()
            ?: ""

        if (otp.isEmpty()) {
            etOtp.error = "Enter OTP"
            etOtp.requestFocus()
            return
        }

        if (otp.length != 6) {
            etOtp.error = "Enter 6-digit OTP"
            etOtp.requestFocus()
            return
        }

        btnVerifyOtp.isEnabled = false

        val request = VerifyOtpRequest(
            email = email,
            otp = otp
        )

        RetrofitClient.authApi
            .verifyOtp(request)
            .enqueue(object : Callback<String> {

                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {

                    btnVerifyOtp.isEnabled = true

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@OTPVerificationActivity,
                            "OTP verified successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(
                            this@OTPVerificationActivity,
                            ResetPasswordActivity::class.java
                        )

                        intent.putExtra(
                            "email",
                            email
                        )

                        intent.putExtra(
                            "otp",
                            otp
                        )

                        startActivity(intent)

                        finish()

                    } else {

                        val message =
                            response.errorBody()
                                ?.string()
                                ?.trim()
                                ?.takeIf {
                                    it.isNotEmpty()
                                }
                                ?: "Invalid OTP"

                        Toast.makeText(
                            this@OTPVerificationActivity,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {

                    btnVerifyOtp.isEnabled = true

                    Toast.makeText(
                        this@OTPVerificationActivity,
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