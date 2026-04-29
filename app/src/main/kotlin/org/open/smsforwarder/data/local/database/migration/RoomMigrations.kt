package org.open.smsforwarder.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

private const val MIGRATION_1_2_START_VERSION = 1
private const val MIGRATION_1_2_END_VERSION = 2

val MIGRATION_1_2 = object : Migration(MIGRATION_1_2_START_VERSION, MIGRATION_1_2_END_VERSION) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `forwarding_table_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `title` TEXT NOT NULL,
                `forwarding_type` TEXT,
                `sender_email` TEXT,
                `recipient_email` TEXT NOT NULL,
                `telegram_api_token` TEXT NOT NULL,
                `telegram_chat_id` TEXT NOT NULL,
                `error_text` TEXT NOT NULL
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO `forwarding_table_new` (
                `id`, `title`, `forwarding_type`, `sender_email`, `recipient_email`, `error_text`,
                `telegram_api_token`, `telegram_chat_id`
            )
            SELECT 
                `id`, `title`, `forwarding_type`, `sender_email`, `recipient_email`, `error_text`,
                '', ''
            FROM `forwarding_table`
        """.trimIndent()
        )

        db.execSQL("DROP TABLE `forwarding_table`")

        db.execSQL("ALTER TABLE `forwarding_table_new` RENAME TO `forwarding_table`")
    }
}

private const val MIGRATION_2_3_START_VERSION = 2
private const val MIGRATION_2_3_END_VERSION = 3

val MIGRATION_2_3 = object : Migration(MIGRATION_2_3_START_VERSION, MIGRATION_2_3_END_VERSION) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            ALTER TABLE `forwarding_table`
            ADD COLUMN `google_chat_web_hook` TEXT NOT NULL DEFAULT ''
            """.trimIndent()
        )
    }
}
