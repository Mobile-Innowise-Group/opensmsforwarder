package org.open.smsforwarder.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {

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
