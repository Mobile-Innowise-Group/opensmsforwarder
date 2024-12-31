package org.open.smsforwarder.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.open.smsforwarder.data.local.database.dao.RulesDao
import org.open.smsforwarder.data.local.database.entity.RuleEntity
import org.open.smsforwarder.data.mapper.toData
import org.open.smsforwarder.data.mapper.toDomain
import org.open.smsforwarder.domain.model.Rule
import javax.inject.Inject

class RulesRepository @Inject constructor(
    private val rulesDao: RulesDao,
) {

    suspend fun getRules(): List<Rule> =
        withContext(Dispatchers.IO) {
            rulesDao
                .getRules()
                .distinct()
                .map(RuleEntity::toDomain)
        }

    fun getRulesFlow(): Flow<List<Rule>> =
        rulesDao
            .getRulesFlow()
            .distinctUntilChanged()
            .map { it.map(RuleEntity::toDomain) }

    suspend fun getRulesByForwardingId(recipientId: Long): List<Rule> =
        withContext(Dispatchers.IO) {
            rulesDao
                .getRulesByForwardingId(recipientId)
                .distinct()
                .map(RuleEntity::toDomain)
        }

    fun getRulesByForwardingIdFlow(id: Long): Flow<List<Rule>> =
        rulesDao
            .getRulesByForwardingIdFlow(id)
            .distinctUntilChanged()
            .map { it.map(RuleEntity::toDomain) }

    suspend fun insertRule(rule: Rule) {
        withContext(Dispatchers.IO) {
            rulesDao.insertRule(rule.toData())
        }
    }

    suspend fun deleteRule(id: Long) {
        withContext(Dispatchers.IO) {
            rulesDao.deleteRule(id)
        }
    }
}
