package org.open.smsforwarder.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.open.smsforwarder.data.local.database.dao.ProcessedMessageDao
import org.open.smsforwarder.data.local.database.entity.ProcessedMessageEntity
import javax.inject.Inject

class ProcessedMessageRepository @Inject constructor(
    private val processedMessageDao: ProcessedMessageDao,
) {
    suspend fun tryMarkProcessed(fingerprint: String, ttlMs: Long): Boolean =
        withContext(Dispatchers.IO) {
            deleteExpired(ttlMs)
            val insertResult = processedMessageDao.insertProcessedMessage(
                ProcessedMessageEntity(
                    fingerprint = fingerprint,
                    createdAt = System.currentTimeMillis()
                )
            )
            insertResult != INSERT_IGNORED
        }

    suspend fun deleteExpired(ttlMs: Long) {
        withContext(Dispatchers.IO) {
            processedMessageDao.deleteOlderThan(System.currentTimeMillis() - ttlMs)
        }
    }

    companion object {
        private const val INSERT_IGNORED = -1L
    }
}
