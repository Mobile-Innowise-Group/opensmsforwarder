package org.open.smsforwarder.data.security

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataCipherTest {

    private val dataCipher = DataCipher()

    @Test
    fun encryptDecrypt_returnsOriginalValue() {
        val source = "Sender:+37529 123-45-67 | message: one-time code 123456"

        val encrypted = dataCipher.encrypt(source)
        val decrypted = dataCipher.decrypt(encrypted)

        requireNotNull(encrypted)
        assertTrue(encrypted.startsWith("enc::"))
        assertNotEquals(source, encrypted)
        assertEquals(source, decrypted)
    }

    @Test
    fun decrypt_plaintext_returnsSameValue() {
        val source = "plain_text_value"

        val decrypted = dataCipher.decrypt(source)

        assertEquals(source, decrypted)
    }

    @Test
    fun encrypt_nullAndEmpty_returnsSameValues() {
        assertNull(dataCipher.encrypt(null))
        assertEquals("", dataCipher.encrypt(""))
    }

    @Test
    fun decrypt_nullAndEmpty_returnsSameValues() {
        assertNull(dataCipher.decrypt(null))
        assertEquals("", dataCipher.decrypt(""))
    }

    @Test
    fun encrypt_alreadyEncrypted_returnsSameCipherText() {
        val source = "already encrypted check"
        val encrypted = dataCipher.encrypt(source)

        val encryptedAgain = dataCipher.encrypt(encrypted)

        assertEquals(encrypted, encryptedAgain)
    }
}
