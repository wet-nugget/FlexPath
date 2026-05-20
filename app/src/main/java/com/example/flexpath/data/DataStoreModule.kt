package com.example.flexpath.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

private const val DATASTORE_NAME = "workouts_prefs"

val Context.dataStore by preferencesDataStore(
    name = DATASTORE_NAME
)
