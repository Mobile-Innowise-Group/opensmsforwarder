package com.github.opensmsforwarder.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.github.opensmsforwarder.data.local.database.entity.RuleEntity
import com.github.opensmsforwarder.data.local.database.entity.RuleEntity.Companion.FORWARDING_ID
import com.github.opensmsforwarder.data.local.database.entity.RuleEntity.Companion.FORWARDING_RULES_TABLE
import com.github.opensmsforwarder.data.local.database.entity.RuleEntity.Companion.ID
import kotlinx.coroutines.flow.Flow

@Dao
interface RulesDao {

    @Query("SELECT * FROM $FORWARDING_RULES_TABLE")
    suspend fun getRules(): List<RuleEntity>

    @Query("SELECT * FROM $FORWARDING_RULES_TABLE")
    fun getRulesFlow(): Flow<List<RuleEntity>>

    @Query("SELECT * FROM $FORWARDING_RULES_TABLE  WHERE $FORWARDING_ID = :id")
    suspend fun getRulesByForwardingId(id: Long): List<RuleEntity>

    @Query("SELECT * FROM $FORWARDING_RULES_TABLE WHERE $FORWARDING_ID = :id")
    fun getRulesByForwardingIdFlow(id: Long): Flow<List<RuleEntity>>

    @Upsert
    suspend fun insertRule(ruleEntity: RuleEntity): Long

    @Query("DELETE FROM $FORWARDING_RULES_TABLE WHERE $ID = :id")
    suspend fun deleteRule(id: Long)
}
