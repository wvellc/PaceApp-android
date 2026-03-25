package com.wvelabs.core_network.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Singleton
class CryptoManager @Inject constructor() {

    private val keyAlias = "app_secure_prefs_key"
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    private fun getSecretKey(): SecretKey {
        val existingKey = keyStore.getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createSecretKey()
    }

    private fun createSecretKey(): SecretKey {
        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val spec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun encrypt(unencryptedText: String): String {
        try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
            val iv = cipher.iv
            val encrypted = cipher.doFinal(unencryptedText.toByteArray())
            // Combine IV and Encrypted data so we can decrypt it later
            val combined = iv + encrypted
            return Base64.encode(combined)
        } catch (e: Exception) {
            return "" // Handle gracefully in DataStore
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun decrypt(encryptedText: String): String {
        try {
            val combined = Base64.decode(encryptedText)
            val iv = combined.copyOfRange(0, 12) // GCM IV is 12 bytes
            val encrypted = combined.copyOfRange(12, combined.size)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)

            return String(cipher.doFinal(encrypted))
        } catch (e: Exception) {
            return "" // If decryption fails (e.g. key invalidated by OS), return empty
        }
    }
}