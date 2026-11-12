package com.lostfound.session

import android.content.Context

class SessionManager(
    context: Context
) {

    private val prefs =
        context.getSharedPreferences(
            "LostFoundPrefs",
            Context.MODE_PRIVATE
        )

    companion object {

        private const val TOKEN =
            "jwt_token"

        private const val FULL_NAME =
            "full_name"

        private const val ROLE =
            "user_role"
    }


    // ============================================================
    // SAVE COMPLETE SESSION
    // ============================================================

    fun saveSession(
        token: String,
        fullName: String,
        role: String
    ) {

        prefs.edit()
            .putString(
                TOKEN,
                token
            )
            .putString(
                FULL_NAME,
                fullName
            )
            .putString(
                ROLE,
                role.trim().uppercase()
            )
            .apply()
    }


    // ============================================================
    // SAVE TOKEN
    // ============================================================

    fun saveToken(
        token: String
    ) {

        prefs.edit()
            .putString(
                TOKEN,
                token
            )
            .apply()
    }


    // ============================================================
    // GET TOKEN
    // ============================================================

    fun getToken(): String? {

        return prefs.getString(
            TOKEN,
            null
        )
    }


    // ============================================================
    // GET FULL NAME
    // ============================================================

    fun getFullName(): String? {

        return prefs.getString(
            FULL_NAME,
            ""
        )
    }


    // ============================================================
    // GET ROLE
    // ============================================================

    fun getRole(): String {

        return prefs.getString(
            ROLE,
            "USER"
        ) ?: "USER"
    }


    // ============================================================
    // CHECK ADMIN
    // ============================================================

    fun isAdmin(): Boolean {

        return getRole().equals(
            "ADMIN",
            ignoreCase = true
        )
    }


    // ============================================================
    // CHECK NORMAL USER
    // ============================================================

    fun isUser(): Boolean {

        return !isAdmin()
    }


    // ============================================================
    // CHECK LOGIN STATUS
    // ============================================================

    fun isLoggedIn(): Boolean {

        return !getToken().isNullOrEmpty()
    }


    // ============================================================
    // CLEAR SESSION
    // ============================================================

    fun clearSession() {

        prefs.edit()
            .clear()
            .apply()
    }
}