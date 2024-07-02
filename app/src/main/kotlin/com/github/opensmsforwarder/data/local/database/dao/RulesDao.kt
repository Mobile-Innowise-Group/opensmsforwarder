package com.github.opensmsforwarder.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.github.opensmsforwarder.data.local.database.entity.RECIPIENT_ID
import com.github.opensmsforwarder.data.local.database.entity.RULES_TABLE
import com.github.opensmsforwarder.data.local.database.entity.RULE_ID_FIELD
import com.github.opensmsforwarder.data.local.database.entity.RuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RulesDao {

    @Query("SELECT * FROM $RULES_TABLE")
    suspend fun getRules(): List<RuleEntity>

    @Query("SELECT * FROM $RULES_TABLE")
    fun getRulesFlow(): Flow<List<RuleEntity>>

    @Query("SELECT * FROM $RULES_TABLE WHERE $RECIPIENT_ID = :recipientId")
    fun getRulesByRecipientIdFlow(recipientId: Long): Flow<List<RuleEntity>>

    @Upsert
    suspend fun insertRule(ruleEntity: RuleEntity): Long

    @Query("DELETE FROM $RULES_TABLE WHERE $RULE_ID_FIELD = :id")
    suspend fun deleteRule(id: Long)
}
