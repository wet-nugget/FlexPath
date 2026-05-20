package com.example.flexpath.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserRepository(private val context: Context) {

    companion object {
        private val KEY_USERNAME = stringPreferencesKey("user_username")
        private val KEY_PASSWORD = stringPreferencesKey("user_password")
    }

    suspend fun getSavedCredentials(): Pair<String?, String?> = withContext(Dispatchers.IO) {
        val prefs = context.dataStore.data
            .map { it[KEY_USERNAME] to it[KEY_PASSWORD] }
            .first()
        prefs
    }

    suspend fun getSavedUsername(): String? = withContext(Dispatchers.IO) {
        context.dataStore.data
            .map { prefs -> prefs[KEY_USERNAME] }
            .first()
    }

    suspend fun saveUser(username: String, password: String) = withContext(Dispatchers.IO) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USERNAME] = username
            prefs[KEY_PASSWORD] = password
        }
    }

    suspend fun clearUser() = withContext(Dispatchers.IO) {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_USERNAME)
            prefs.remove(KEY_PASSWORD)
        }
    }
}
