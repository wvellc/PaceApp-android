package com.wvelabs.core_network.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * The generic storage engine.
 * All methods are `protected` to ensure the UI layer interacts with AppSessionManager,
 * not raw DataStore keys.
 */
abstract class CoreDataStore(
    private val dataStore: DataStore<Preferences>
) {
    // --- Standard Read/Write (Plain Text) ---

    protected suspend fun <T> write(key: Preferences.Key<T>, value: T) {
        dataStore.edit { it[key] = value }
    }

    // READ AS STREAM: Combines your 'read' and 'observe' into one robust, idiomatic flow
    protected fun <T> observe(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return dataStore.data
            .catch { exception ->
                // Idiomatic Kotlin way to handle corrupted/missing files
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[key] ?: defaultValue
            }
    }

    // READ ONE-TIME: Good for functions that just need the value once (like interceptors)
    protected suspend fun <T> readOnce(key: Preferences.Key<T>): T? {
        return dataStore.data.map { preferences -> preferences[key] }.firstOrNull()
    }

    // --- Secure Read/Write (Requires CryptoManager at compile time) ---

    protected suspend fun writeEncrypted(
        key: Preferences.Key<String>,
        value: String,
        cryptoManager: CryptoManager
    ) {
        val encryptedValue = cryptoManager.encrypt(value)
        dataStore.edit { it[key] = encryptedValue }
    }

    protected fun observeEncrypted(
        key: Preferences.Key<String>,
        defaultValue: String,
        cryptoManager: CryptoManager
    ): Flow<String> {
        return observe(key, defaultValue).map { storedValue ->
            if (storedValue == defaultValue) return@map defaultValue

            val decrypted = cryptoManager.decrypt(storedValue)
            decrypted.ifEmpty { defaultValue }
        }
    }

    // --- Deletion ---

    // DELETE SPECIFIC KEY
    protected suspend fun <T> delete(key: Preferences.Key<T>) {
        dataStore.edit { preferences -> preferences.remove(key) }
    }

    // ERASE ALL EXCEPT IGNORED (Great for clean logouts)
    protected suspend fun clearAll(ignoredKeys: List<Preferences.Key<*>>) {
        dataStore.edit { preferences ->
            val keysToRemove = preferences.asMap().keys.filter { it !in ignoredKeys }
            keysToRemove.forEach { preferences.remove(it) }
        }
    }
}