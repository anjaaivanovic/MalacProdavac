package com.example.front.helper.DataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nimbusds.jwt.SignedJWT
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreManager @Inject constructor(@ApplicationContext context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token_datastore")
    private val dataStore: DataStore<Preferences> = context.dataStore

    private val TOKEN_KEY = stringPreferencesKey("token")
    private val FCM_KEY = stringPreferencesKey("fcm")

    suspend fun storeToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    val tokenFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    suspend fun storeFCM(token: String) {
        dataStore.edit { preferences ->
            preferences[FCM_KEY] = token
        }
    }

    val fcmFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[FCM_KEY]
    }

    suspend fun getUserIdFromToken(): Int? {
        val token = this.tokenFlow.first()

        return decodeTokenAndGetUserId(token)
    }

    private fun decodeTokenAndGetUserId(token: String?): Int? {
        if (token.isNullOrBlank()) {
            return null
        }

        try {
            val signedJWT = SignedJWT.parse(token)
            val claimsSet = signedJWT.jwtClaimsSet

            val subClaim = claimsSet.getClaim("sub") as? String

            if (subClaim?.toIntOrNull() != null) {
                return subClaim.toInt()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return null
    }

    suspend fun setFirstTime()
    {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey("firstTimeInApp")] = "false"
        }
    }

    suspend fun isFirstTime(): Boolean
    {
        val result = dataStore.data.first()[stringPreferencesKey("firstTimeInApp")]
        if (result != null){
            return false
        }
        return true
    }

    suspend fun isLoggedIn(): Boolean
    {
        val result = dataStore.data.first()[TOKEN_KEY]
        if (result != null && result != ""){
            return true
        }
        return false
    }

    suspend fun getRoleId(): Int?
    {
        val token = this.tokenFlow.first()

        return decodeTokenAndGetUserRoleId(token)
    }

    private fun decodeTokenAndGetUserRoleId(token: String?): Int? {
        if (token.isNullOrBlank()) {
            return null
        }

        try {
            val signedJWT = SignedJWT.parse(token)
            val claimsSet = signedJWT.jwtClaimsSet

            val roleClaim = claimsSet.getClaim("role") as? String

            if (roleClaim?.toIntOrNull() != null) {
                return roleClaim.toInt()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return null
    }
}
