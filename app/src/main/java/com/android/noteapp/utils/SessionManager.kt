package com.android.noteapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.android.noteapp.data.remote.models.Role
import com.android.noteapp.utils.Constants.EMAIL_KEY
import com.android.noteapp.utils.Constants.JWT_TOKEN_KEY
import com.android.noteapp.utils.Constants.NAME_KEY
import com.android.noteapp.utils.Constants.ROLE_KEY
import kotlinx.coroutines.flow.first

class SessionManager(val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("session_manager")

    suspend fun updateSession(token: String, name: String, email: String, role: Role) {
        val jwtTokenKey = stringPreferencesKey(JWT_TOKEN_KEY)
        val nameKey = stringPreferencesKey(NAME_KEY)
        val emailKey = stringPreferencesKey(EMAIL_KEY)
        val roleKey = stringPreferencesKey(ROLE_KEY)
        context.dataStore.edit { preferences ->
            preferences[jwtTokenKey] = token
            preferences[nameKey] = name
            preferences[emailKey] = email
            preferences[roleKey] = role.toString()
        }
    }

    suspend fun getJwtToken(): String? {
        val jwtTokenKey = stringPreferencesKey(JWT_TOKEN_KEY)
        val preferences = context.dataStore.data.first()

        return preferences[jwtTokenKey]
    }


    suspend fun getCurrentUserName(): String? {
        val nameKey = stringPreferencesKey(NAME_KEY)
        val preferences = context.dataStore.data.first()
        return preferences[nameKey]
    }


    suspend fun getCurrentUserEmail(): String? {
        val emailKey = stringPreferencesKey(EMAIL_KEY)
        val preferences = context.dataStore.data.first()
        return preferences[emailKey]
    }

    suspend fun getCurrentUserRole(): String? {
        val roleKey = stringPreferencesKey(ROLE_KEY)
        val preferences = context.dataStore.data.first()
        return preferences[roleKey]
    }

    suspend fun logout() {
        context.dataStore.edit {
            it.clear()
        }
    }
}