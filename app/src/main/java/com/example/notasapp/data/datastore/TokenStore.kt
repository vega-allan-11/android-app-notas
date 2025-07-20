package com.example.notasapp.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("session")

object TokenStore {
    private val TOKEN_KEY = stringPreferencesKey("token")

    fun getToken(context: Context): Flow<String?> {
        return context.dataStore.data.map { prefs -> prefs[TOKEN_KEY] }
    }

    suspend fun saveToken(context: Context, token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    suspend fun clearToken(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }
}
