package com.lostfound

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView

import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

import com.lostfound.model.RegisterRequest
import com.lostfound.network.RetrofitClient

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity : AppCompatActivity() {

    // ============================================================
    // VIEWS
    // ============================================================

    private lateinit var registerScrollView:
            NestedScrollView

    private lateinit var etFullName:
            TextInputEditText

    private lateinit var etEmail:
            TextInputEditText

    private lateinit var etPhone:
            TextInputEditText

    private lateinit var etPassword:
            TextInputEditText

    private lateinit var etConfirmPassword:
            TextInputEditText

    private lateinit var btnRegister:
            MaterialButton

    private lateinit var tvLogin:
            TextView


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
            R.layout.activity_register
        )


        // ========================================================
        // FIND VIEWS
        // ========================================================

        registerScrollView =
            findViewById(
                R.id.registerScrollView
            )

        etFullName =
            findViewById(
                R.id.etFullName
            )

        etEmail =
            findViewById(
                R.id.etEmail
            )

        etPhone =
            findViewById(
                R.id.etPhone
            )

        etPassword =
            findViewById(
                R.id.etPassword
            )

        etConfirmPassword =
            findViewById(
                R.id.etConfirmPassword
            )

        btnRegister =
            findViewById(
                R.id.btnRegister
            )

        tvLogin =
            findViewById(
                R.id.tvLogin
            )


        // ========================================================
        // KEYBOARD / WINDOW INSETS
        // ========================================================

        setupKeyboardHandling()


        // ========================================================
        // REGISTER BUTTON
        // ========================================================

        btnRegister.setOnClickListener {

            registerUser()
        }


        // ========================================================
        // LOGIN
        // ========================================================

        tvLogin.setOnClickListener {

            finish()
        }
    }


    // ============================================================
    // KEYBOARD HANDLING
    // ============================================================

    private fun setupKeyboardHandling() {

        ViewCompat.setOnApplyWindowInsetsListener(
            registerScrollView
        ) { view, insets ->

            val imeInsets =
                insets.getInsets(
                    WindowInsetsCompat.Type.ime()
                )

            val systemInsets =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )


            // ====================================================
            // CALCULATE BOTTOM SPACE
            // ====================================================

            val bottomPadding =
                maxOf(
                    imeInsets.bottom,
                    systemInsets.bottom
                ) + 32


            // ====================================================
            // ADD SPACE ABOVE KEYBOARD
            // ====================================================

            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                bottomPadding
            )


            insets
        }


        // ========================================================
        // PASSWORD FOCUS
        // ========================================================

        etPassword.setOnFocusChangeListener { view, hasFocus ->

            if (hasFocus) {

                scrollToField(
                    view
                )
            }
        }


        // ========================================================
        // CONFIRM PASSWORD FOCUS
        // ========================================================

        etConfirmPassword.setOnFocusChangeListener { view, hasFocus ->

            if (hasFocus) {

                scrollToField(
                    view
                )
            }
        }
    }


    // ============================================================
    // SCROLL FIELD ABOVE KEYBOARD
    // ============================================================

    private fun scrollToField(
        field: View
    ) {

        field.postDelayed({

            val location =
                IntArray(2)

            field.getLocationOnScreen(
                location
            )


            val fieldTop =
                location[1]

            val fieldBottom =
                fieldTop + field.height


            // ====================================================
            // GET KEYBOARD TOP
            // ====================================================

            val rootHeight =
                registerScrollView.rootView.height


            val visibleHeight =
                registerScrollView.height


            val keyboardTop =
                rootHeight -
                        getKeyboardHeight()


            // ====================================================
            // EXTRA SPACE
            // ====================================================

            val extraSpace =
                80


            // ====================================================
            // IF FIELD IS BEHIND KEYBOARD
            // ====================================================

            if (
                fieldBottom >
                keyboardTop - extraSpace
            ) {

                val amountToScroll =
                    fieldBottom -
                            keyboardTop +
                            extraSpace


                registerScrollView.smoothScrollBy(
                    0,
                    amountToScroll
                )
            }

        }, 250)
    }


    // ============================================================
    // GET KEYBOARD HEIGHT
    // ============================================================

    private fun getKeyboardHeight(): Int {

        val rootView =
            registerScrollView.rootView


        val visibleRect =
            android.graphics.Rect()

        rootView.getWindowVisibleDisplayFrame(
            visibleRect
        )


        return rootView.height -
                visibleRect.bottom
    }


    // ============================================================
    // REGISTER USER
    // ============================================================

    private fun registerUser() {

        val fullName =
            etFullName.text
                ?.toString()
                ?.trim()
                ?: ""


        val email =
            etEmail.text
                ?.toString()
                ?.trim()
                ?.lowercase()
                ?: ""


        val phone =
            etPhone.text
                ?.toString()
                ?.trim()
                ?: ""


        val password =
            etPassword.text
                ?.toString()
                ?: ""


        val confirmPassword =
            etConfirmPassword.text
                ?.toString()
                ?: ""


        // ========================================================
        // FULL NAME
        // ========================================================

        if (fullName.isEmpty()) {

            etFullName.error =
                "Enter your full name"

            etFullName.requestFocus()

            return
        }


        // ========================================================
        // EMAIL
        // ========================================================

        if (email.isEmpty()) {

            etEmail.error =
                "Enter your email"

            etEmail.requestFocus()

            return
        }


        if (
            !android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()
        ) {

            etEmail.error =
                "Enter a valid email"

            etEmail.requestFocus()

            return
        }


        // ========================================================
        // PHONE
        // ========================================================

        if (phone.isEmpty()) {

            etPhone.error =
                "Enter your phone number"

            etPhone.requestFocus()

            return
        }


        if (
            !phone.matches(
                Regex("^[0-9]{10}$")
            )
        ) {

            etPhone.error =
                "Enter a valid 10-digit phone number"

            etPhone.requestFocus()

            return
        }


        // ========================================================
        // PASSWORD
        // ========================================================

        if (password.isEmpty()) {

            etPassword.error =
                "Enter a password"

            etPassword.requestFocus()

            return
        }


        if (password.length < 6) {

            etPassword.error =
                "Password must contain at least 6 characters"

            etPassword.requestFocus()

            return
        }


        // ========================================================
        // CONFIRM PASSWORD
        // ========================================================

        if (confirmPassword.isEmpty()) {

            etConfirmPassword.error =
                "Confirm your password"

            etConfirmPassword.requestFocus()

            return
        }


        if (password != confirmPassword) {

            etConfirmPassword.error =
                "Passwords do not match"

            etConfirmPassword.requestFocus()

            return
        }


        // ========================================================
        // DISABLE BUTTON
        // ========================================================

        btnRegister.isEnabled =
            false


        // ========================================================
        // CREATE REQUEST
        // ========================================================

        val request =
            RegisterRequest(
                fullName = fullName,
                email = email,
                phone = phone,
                password = password
            )


        // ========================================================
        // API CALL
        // ========================================================

        RetrofitClient.authApi
            .register(request)
            .enqueue(
                object : Callback<String> {

                    override fun onResponse(
                        call: Call<String>,
                        response: Response<String>
                    ) {

                        btnRegister.isEnabled =
                            true


                        if (
                            response.isSuccessful
                        ) {

                            Toast.makeText(
                                this@RegisterActivity,
                                "Registration successful. Check your email.",
                                Toast.LENGTH_LONG
                            ).show()


                            startActivity(
                                Intent(
                                    this@RegisterActivity,
                                    LoginActivity::class.java
                                )
                            )

                            finish()

                        } else {

                            val errorMessage =
                                response
                                    .errorBody()
                                    ?.string()
                                    ?.trim()
                                    ?.takeIf {
                                        it.isNotEmpty()
                                    }
                                    ?: "Registration failed."


                            Toast.makeText(
                                this@RegisterActivity,
                                errorMessage,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }


                    override fun onFailure(
                        call: Call<String>,
                        t: Throwable
                    ) {

                        btnRegister.isEnabled =
                            true


                        Toast.makeText(
                            this@RegisterActivity,
                            "Connection failed: ${
                                t.localizedMessage
                                    ?: "Network Error"
                            }",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }
}