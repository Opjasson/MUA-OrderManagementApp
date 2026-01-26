package com.example.cafecornerapp.DataStore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.get

class UserPreference(private val context: Context) {

    private val USER_ID = stringPreferencesKey("user_id")

    // SIMPAN userId
    suspend fun saveUserId(userId: String) {
        context.dataStore.edit {
            it[USER_ID] = userId
        }
    }

    // AMBIL userId
    fun getUserId(): Flow<String> {
        return context.dataStore.data.map {
            it[USER_ID] ?: ""
        }
    }

    suspend fun deleteUserId () {
        context.dataStore.edit {
            it.remove(USER_ID)
        }
    }
}