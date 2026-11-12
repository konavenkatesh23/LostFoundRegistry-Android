package com.lostfound

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.lostfound.model.ResetPasswordRequest
import com.lostfound.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var etNewPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnResetPassword: MaterialButton

    private lateinit var email: String
    private lateinit var otp: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_reset_password)

        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnResetPassword = findViewById(R.id.btnResetPassword)

        email = intent.getStringExtra("email") ?: ""
        otp = intent.getStringExtra("otp") ?: ""

        if (email.isEmpty() || otp.isEmpty()) {

            Toast.makeText(
                this,
                "Invalid password reset session",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }

        btnResetPassword.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword() {

        val newPassword = etNewPassword.text
            ?.toString()
            ?: ""

        val confirmPassword = etConfirmPassword.text
            ?.toString()
            ?: ""

        if (newPassword.isEmpty()) {

            etNewPassword.error =
                "Enter new password"

            etNewPassword.requestFocus()

            return
        }

        if (newPassword.length < 6) {

            etNewPassword.error =
                "Password must contain at least 6 characters"

            etNewPassword.requestFocus()

            return
        }

        if (confirmPassword.isEmpty()) {

            etConfirmPassword.error =
                "Confirm your password"

            etConfirmPassword.requestFocus()

            return
        }

        if (newPassword != confirmPassword) {

            etConfirmPassword.error =
                "Passwords do not match"

            etConfirmPassword.requestFocus()

            return
        }

        btnResetPassword.isEnabled = false

        val request = ResetPasswordRequest(
            email = email,
            otp = otp,
            newPassword = newPassword
        )

        RetrofitClient.authApi
            .resetPassword(request)
            .enqueue(object : Callback<String> {

                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {

                    btnResetPassword.isEnabled = true

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Password reset successfully",
                            Toast.LENGTH_LONG
                        ).show()

                        val intent = Intent(
                            this@ResetPasswordActivity,
                            LoginActivity::class.java
                        )

                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_NEW_TASK

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
                                ?: "Password reset failed"

                        Toast.makeText(
                            this@ResetPasswordActivity,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {

                    btnResetPassword.isEnabled = true

                    Toast.makeText(
                        this@ResetPasswordActivity,
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