package com.github.opensmsforwarder.data

import com.github.opensmsforwarder.data.local.database.dao.RulesDao
import com.github.opensmsforwarder.data.local.prefs.Prefs
import com.github.opensmsforwarder.data.mapper.Mapper
import com.github.opensmsforwarder.model.Rule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RulesRepository @Inject constructor(
    private val rulesDao: RulesDao,
    private val prefs: Prefs,
    private val mapper: Mapper,
) {

    fun getRulesForCurrentRecipientFlow(): Flow<List<Rule>> =
        rulesDao
            .getRulesByRecipientIdFlow(prefs.currentRecipientId)
            .map { it.map(mapper::toRule) }

    fun getRulesFlow(): Flow<List<Rule>> =
        rulesDao
            .getRulesFlow()
            .map { it.map(mapper::toRule) }

    suspend fun getRules(): List<Rule> =
        rulesDao
            .getRules()
            .map(mapper::toRule)

    suspend fun insertRule(rule: Rule) {
        withContext(Dispatchers.IO) {
            rulesDao.insertRule(mapper.toRuleEntity(rule))
        }
    }

    suspend fun deleteRule(id: Long) {
        withContext(Dispatchers.IO) {
            rulesDao.deleteRule(id)
        }
    }
}
