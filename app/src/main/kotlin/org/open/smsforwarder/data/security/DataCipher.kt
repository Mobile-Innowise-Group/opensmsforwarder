package org.open.smsforwarder.data.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataCipher @Inject constructor() {

    fun encrypt(value: String?): String? {
        val shouldSkipEncryption = value == null || value.isEmpty() || value.startsWith(ENCRYPTED_PREFIX)
        return if (shouldSkipEncryption) {
            value
        } else {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
            val iv = cipher.iv
            val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
            val ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)
            val encryptedBase64 = Base64.encodeToString(encrypted, Base64.NO_WRAP)
            "$ENCRYPTED_PREFIX$ivBase64$SEPARATOR$encryptedBase64"
        }
    }

    fun decrypt(value: String?): String? {
        val shouldSkipDecryption = value == null || value.isEmpty() || !value.startsWith(ENCRYPTED_PREFIX)
        return if (shouldSkipDecryption) {
            value
        } else {
            runCatching {
                val encryptedData = value.removePrefix(ENCRYPTED_PREFIX)
                val separatorIndex = encryptedData.indexOf(SEPARATOR)
                if (separatorIndex <= 0 || separatorIndex >= encryptedData.lastIndex) {
                    value
                } else {
                    val ivBase64 = encryptedData.substring(0, separatorIndex)
                    val payloadBase64 = encryptedData.substring(separatorIndex + 1)
                    val iv = Base64.decode(ivBase64, Base64.NO_WRAP)
                    val payload = Base64.decode(payloadBase64, Base64.NO_WRAP)

                    val cipher = Cipher.getInstance(TRANSFORMATION)
                    val spec = GCMParameterSpec(TAG_LENGTH_BITS, iv)
                    cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), spec)
                    val decrypted = cipher.doFinal(payload)
                    String(decrypted, Charsets.UTF_8)
                }
            }.getOrElse { value }
        }
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val existingKey = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
        if (existingKey != null) return existingKey

        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true)
            .build()
        keyGenerator.init(keySpec)
        return keyGenerator.generateKey()
    }

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "opensmsforwarder_data_key_v1"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val TAG_LENGTH_BITS = 128
        const val ENCRYPTED_PREFIX = "enc::"
        const val SEPARATOR = ":"
    }
}
