package org.open.smsforwarder.processing.dedup

import org.open.smsforwarder.data.repository.ProcessedMessageRepository
import org.open.smsforwarder.extension.normalizeSpaces
import java.security.MessageDigest
import javax.inject.Inject

class SmsDeduplicationManager @Inject constructor(
    private val processedMessageRepository: ProcessedMessageRepository,
) {
    suspend fun shouldProcess(sender: String?, message: String): Boolean {
        val normalizedSender = sender.orEmpty().trim().lowercase()
        val normalizedMessage = message.normalizeSpaces().trim()
        val fingerprint = "$normalizedSender|$normalizedMessage".sha256()
        return processedMessageRepository.tryMarkProcessed(fingerprint, DEDUP_WINDOW_MS)
    }

    private fun String.sha256(): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(toByteArray())
        return digest.joinToString("") { byte -> "%02x".format(byte) }
    }

    companion object {
        private const val DEDUP_WINDOW_MS = 2 * 60 * 1000L
    }
}
