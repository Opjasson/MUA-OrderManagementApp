package com.example.cafecornerapp.DataStore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(
    name = "user_pref"
)