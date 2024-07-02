package com.github.opensmsforwarder.data

import com.github.opensmsforwarder.data.local.database.dao.RecipientsDao
import com.github.opensmsforwarder.data.local.prefs.Prefs
import com.github.opensmsforwarder.data.mapper.Mapper
import com.github.opensmsforwarder.model.Recipient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecipientsRepository @Inject constructor(
    private val recipientsDao: RecipientsDao,
    private val prefs: Prefs,
    private val mapper: Mapper,
) {

    fun getCurrentRecipientId() = prefs.currentRecipientId

    fun setCurrentRecipientId(id: Long) {
        synchronized(this) {
            prefs.currentRecipientId = id
        }
    }

    fun getRecipientsFlow(): Flow<List<Recipient>> =
        recipientsDao
            .getRecipientsFlow()
            .distinctUntilChanged()
            .map { recipients -> recipients.map(mapper::toRecipient) }

    fun getCurrentRecipientFlow(): Flow<Recipient> =
        recipientsDao
            .getRecipientByIdFlow(prefs.currentRecipientId)
            .filterNotNull()
            .distinctUntilChanged()
            .map(mapper::toRecipient)

    suspend fun getRecipientById(id: Long): Recipient? =
        withContext(Dispatchers.IO) {
            recipientsDao.getRecipientById(id)?.let {
                mapper.toRecipient(it)
            }
        }

    suspend fun insertOrUpdateRecipient(recipient: Recipient) =
        withContext(Dispatchers.IO) {
            recipientsDao.upsertRecipient(mapper.toRecipientEntity(recipient))
        }

    suspend fun deleteRecipient(id: Long) {
        withContext(Dispatchers.IO) {
            recipientsDao.deleteRecipient(id)
        }
    }

    companion object {
        const val NO_ID = -1L
    }
}
