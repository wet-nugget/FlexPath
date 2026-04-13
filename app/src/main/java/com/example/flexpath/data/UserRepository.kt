package com.example.flexpath.data

import android.content.Context

class UserRepository(private val context: Context) {
    private val prefsName = "UserPrefs"
    private val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    fun saveUser(username: String, password: String) {
        prefs.edit().putString("username", username).putString("password", password).apply()
    }

    fun getSavedUsername(): String? = prefs.getString("username", null)
    fun getSavedPassword(): String? = prefs.getString("password", null)
    fun clear() = prefs.edit().clear().apply()
}
