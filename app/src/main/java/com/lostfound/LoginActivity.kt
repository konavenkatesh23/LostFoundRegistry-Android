package com.lostfound

import android.content.Intent
import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.lostfound.databinding.ActivityLoginBinding
import com.lostfound.model.LoginRequest
import com.lostfound.model.LoginResponse
import com.lostfound.network.RetrofitClient
import com.lostfound.session.SessionManager

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    // ============================================================
    // VIEW BINDING
    // ============================================================

    private lateinit var binding: ActivityLoginBinding


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


        // ========================================================
        // View Binding
        // ========================================================

        binding =
            ActivityLoginBinding.inflate(
                layoutInflater
            )

        setContentView(
            binding.root
        )


        // ========================================================
        // Session Manager
        // ========================================================

        sessionManager =
            SessionManager(this)


        // ========================================================
        // CHECK EXISTING SESSION
        // ========================================================

        if (
            sessionManager.isLoggedIn()
        ) {

            openCorrectDashboard()

            return
        }


        // ============================================================
        // LOGIN BUTTON
        // ============================================================

        binding.btnLogin.setOnClickListener {

            val email =
                binding.etEmail.text
                    ?.toString()
                    ?.trim()
                    ?.lowercase()
                    ?: ""


            val password =
                binding.etPassword.text
                    ?.toString()
                    ?: ""


            // ========================================================
            // VALIDATE EMAIL
            // ========================================================

            if (
                email.isEmpty()
            ) {

                binding.etEmail.error =
                    "Enter your email"

                binding.etEmail.requestFocus()

                return@setOnClickListener
            }


            if (
                !android.util.Patterns.EMAIL_ADDRESS
                    .matcher(email)
                    .matches()
            ) {

                binding.etEmail.error =
                    "Enter a valid email"

                binding.etEmail.requestFocus()

                return@setOnClickListener
            }


            // ========================================================
            // VALIDATE PASSWORD
            // ========================================================

            if (
                password.isEmpty()
            ) {

                binding.etPassword.error =
                    "Enter your password"

                binding.etPassword.requestFocus()

                return@setOnClickListener
            }


            // ========================================================
            // LOGIN
            // ========================================================

            loginUser(
                email,
                password
            )
        }


        // ============================================================
        // REGISTER
        // ============================================================

        binding.tvRegister.setOnClickListener {

            startActivity(
                Intent(
                    this@LoginActivity,
                    RegisterActivity::class.java
                )
            )
        }


        // ============================================================
        // FORGOT PASSWORD
        // ============================================================

        binding.tvForgotPassword.setOnClickListener {

            startActivity(
                Intent(
                    this@LoginActivity,
                    ForgotPasswordActivity::class.java
                )
            )
        }
    }


    // ============================================================
    // LOGIN USER
    // ============================================================

    private fun loginUser(
        email: String,
        password: String
    ) {

        // ========================================================
        // Disable login button
        // ========================================================

        binding.btnLogin.isEnabled =
            false


        // ========================================================
        // Create Request
        // ========================================================

        val request =
            LoginRequest(
                email = email,
                password = password
            )


        // ========================================================
        // Retrofit Login Request
        // ========================================================

        RetrofitClient.authApi
            .login(request)
            .enqueue(
                object : Callback<LoginResponse> {

                    // ====================================================
                    // SUCCESS RESPONSE
                    // ====================================================

                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {

                        // ------------------------------------------------
                        // Enable button
                        // ------------------------------------------------

                        binding.btnLogin.isEnabled =
                            true


                        // ------------------------------------------------
                        // Check response
                        // ------------------------------------------------

                        if (
                            response.isSuccessful &&
                            response.body() != null
                        ) {

                            val loginResponse =
                                response.body()!!


                            // ============================================
                            // VALIDATE TOKEN
                            // ============================================

                            if (
                                loginResponse.token
                                    .isBlank()
                            ) {

                                Toast.makeText(
                                    this@LoginActivity,
                                    "Login failed: Invalid token received.",
                                    Toast.LENGTH_LONG
                                ).show()

                                return
                            }


                            // ============================================
                            // NORMALIZE ROLE
                            // ============================================

                            val role =
                                loginResponse.role
                                    .trim()
                                    .uppercase()


                            // ============================================
                            // VALIDATE ROLE
                            // ============================================

                            if (
                                role != "ADMIN" &&
                                role != "USER"
                            ) {

                                Toast.makeText(
                                    this@LoginActivity,
                                    "Login failed: Invalid user role.",
                                    Toast.LENGTH_LONG
                                ).show()

                                return
                            }


                            // ============================================
                            // SAVE SESSION
                            // ============================================

                            sessionManager.saveSession(
                                token =
                                    loginResponse.token,

                                fullName =
                                    loginResponse.fullName,

                                role =
                                    role
                            )


                            // ============================================
                            // SUCCESS MESSAGE
                            // ============================================

                            Toast.makeText(
                                this@LoginActivity,
                                loginResponse.message,
                                Toast.LENGTH_SHORT
                            ).show()


                            // ============================================
                            // OPEN CORRECT DASHBOARD
                            // ============================================

                            openCorrectDashboard()

                        } else {

                            // ============================================
                            // LOGIN FAILED
                            // ============================================

                            Toast.makeText(
                                this@LoginActivity,
                                "Invalid Email or Password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                    // ====================================================
                    // NETWORK FAILURE
                    // ====================================================

                    override fun onFailure(
                        call: Call<LoginResponse>,
                        t: Throwable
                    ) {

                        binding.btnLogin.isEnabled =
                            true


                        Toast.makeText(
                            this@LoginActivity,
                            "Connection Failed: ${
                                t.localizedMessage
                                    ?: "Network Error"
                            }",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }


    // ============================================================
    // OPEN CORRECT DASHBOARD
    // ============================================================

    private fun openCorrectDashboard() {

        val nextActivity: Class<*> =

            if (
                sessionManager.isAdmin()
            ) {

                AdminDashboardActivity::class.java

            } else {

                DashboardActivity::class.java
            }


        startActivity(
            Intent(
                this@LoginActivity,
                nextActivity
            )
        )


        // Prevent returning to login
        finish()
    }
}