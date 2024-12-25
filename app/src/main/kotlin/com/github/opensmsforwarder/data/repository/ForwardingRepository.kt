package com.github.opensmsforwarder.data.repository

import com.github.opensmsforwarder.data.local.database.dao.ForwardingDao
import com.github.opensmsforwarder.data.local.database.entity.ForwardingEntity
import com.github.opensmsforwarder.data.mapper.toData
import com.github.opensmsforwarder.data.mapper.toDomain
import com.github.opensmsforwarder.domain.model.Forwarding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ForwardingRepository @Inject constructor(
    private val forwardingDao: ForwardingDao,
) {

    fun getForwardingFlow(): Flow<List<Forwarding>> =
        forwardingDao
            .getForwardingFlow()
            .distinctUntilChanged()
            .map { forwardingEntity -> forwardingEntity.map(ForwardingEntity::toDomain) }

    suspend fun getForwardingById(id: Long): Forwarding? =
        withContext(Dispatchers.IO) {
            forwardingDao.getForwardingById(id)?.toDomain()
        }

    fun getForwardingByIdFlow(id: Long): Flow<Forwarding> =
        forwardingDao
            .getForwardingByIdFlow(id)
            .filterNotNull()
            .distinctUntilChanged()
            .map(ForwardingEntity::toDomain)

    suspend fun createNewForwarding(): Long = insertOrUpdateForwarding(Forwarding())

    suspend fun insertOrUpdateForwarding(forwarding: Forwarding): Long =
        withContext(Dispatchers.IO) {
            forwardingDao.upsertForwarding(forwarding.toData())
        }

    suspend fun deleteForwarding(id: Long) {
        withContext(Dispatchers.IO) {
            forwardingDao.deleteForwarding(id)
        }
    }
}
