package org.open.smsforwarder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.open.smsforwarder.data.local.database.AppDatabase
import org.open.smsforwarder.data.local.database.migration.MIGRATION_1_2
import org.open.smsforwarder.data.local.database.migration.MIGRATION_2_3
import org.open.smsforwarder.data.local.database.migration.MIGRATION_3_4

@RunWith(AndroidJUnit4::class)
class RoomMigrationTest {

    private lateinit var helper: MigrationTestHelper

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        helper = MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            AppDatabase::class.java,
            emptyList(),
            FrameworkSQLiteOpenHelperFactory()
        )
    }

    @Test
    fun migrate1To2_removesRecipientPhone_addsTelegramFields() {
        val db = helper.createDatabase("test_db", 1)

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `forwarding_table` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `title` TEXT NOT NULL,
                `forwarding_type` TEXT,
                `sender_email` TEXT,
                `recipient_phone` TEXT NOT NULL,
                `recipient_email` TEXT NOT NULL,
                `error_text` TEXT NOT NULL
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO `forwarding_table` 
            (`id`, `title`, `forwarding_type`, `sender_email`, `recipient_phone`, `recipient_email`, `error_text`)
            VALUES (1, 'Test Title', 'EMAIL', 'sender@example.com', '+123456789', 'recipient@example.com', 'none')
        """.trimIndent()
        )

        db.close()

        // Step 2: Run migration to version 2
        val migratedDb = helper.runMigrationsAndValidate(
            "test_db",
            2,
            true,
            MIGRATION_1_2
        )

        // Step 3: Validate migrated data
        val cursor = migratedDb.query("SELECT * FROM forwarding_table")
        assertTrue(cursor.moveToFirst())

        assertEquals(1, cursor.getLong(cursor.getColumnIndexOrThrow("id")))
        assertEquals("Test Title", cursor.getString(cursor.getColumnIndexOrThrow("title")))
        assertEquals("EMAIL", cursor.getString(cursor.getColumnIndexOrThrow("forwarding_type")))
        assertEquals("sender@example.com", cursor.getString(cursor.getColumnIndexOrThrow("sender_email")))
        assertEquals("recipient@example.com", cursor.getString(cursor.getColumnIndexOrThrow("recipient_email")))
        assertEquals("none", cursor.getString(cursor.getColumnIndexOrThrow("error_text")))

        // Verify new fields were added with default values
        assertEquals("", cursor.getString(cursor.getColumnIndexOrThrow("telegram_api_token")))
        assertEquals("", cursor.getString(cursor.getColumnIndexOrThrow("telegram_chat_id")))

        // Verify removed field is truly gone
        assertThrows(IllegalArgumentException::class.java) {
            cursor.getColumnIndexOrThrow("recipient_phone")
        }

        cursor.close()
    }

    @Test
    fun migrate2To3_addsGoogleChatWebhookWithDefaultValue() {
        val db = helper.createDatabase("test_db_2_3", 2)

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `forwarding_table` (
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
            INSERT INTO `forwarding_table`
            (`id`, `title`, `forwarding_type`, `sender_email`, `recipient_email`, `telegram_api_token`, `telegram_chat_id`, `error_text`)
            VALUES (1, 'GC Title', 'GOOGLE_CHAT', null, 'recipient@example.com', '', '', 'none')
        """.trimIndent()
        )

        db.close()

        val migratedDb = helper.runMigrationsAndValidate(
            "test_db_2_3",
            3,
            true,
            MIGRATION_2_3
        )

        val cursor = migratedDb.query("SELECT * FROM forwarding_table WHERE id = 1")
        assertTrue(cursor.moveToFirst())
        assertEquals("", cursor.getString(cursor.getColumnIndexOrThrow("google_chat_web_hook")))
        assertEquals("GC Title", cursor.getString(cursor.getColumnIndexOrThrow("title")))
        assertEquals("GOOGLE_CHAT", cursor.getString(cursor.getColumnIndexOrThrow("forwarding_type")))
        cursor.close()
    }

    @Test
    fun migrate3To4_addsProcessedMessagesTableWithUniqueFingerprintIndex() {
        val db = helper.createDatabase("test_db_3_4", 3)
        db.close()

        val migratedDb = helper.runMigrationsAndValidate(
            "test_db_3_4",
            4,
            true,
            MIGRATION_3_4
        )

        migratedDb.execSQL(
            """
            INSERT INTO `processed_messages_table` (`fingerprint`, `created_at`)
            VALUES ('fp-1', 1000)
            """.trimIndent()
        )

        assertThrows(android.database.sqlite.SQLiteConstraintException::class.java) {
            migratedDb.execSQL(
                """
                INSERT INTO `processed_messages_table` (`fingerprint`, `created_at`)
                VALUES ('fp-1', 2000)
                """.trimIndent()
            )
        }
    }

    @Test
    fun migrate1To4_preservesForwardingDataAndCreatesProcessedMessagesTable() {
        val db = helper.createDatabase("test_db_1_4", 1)

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `forwarding_table` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `title` TEXT NOT NULL,
                `forwarding_type` TEXT,
                `sender_email` TEXT,
                `recipient_phone` TEXT NOT NULL,
                `recipient_email` TEXT NOT NULL,
                `error_text` TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO `forwarding_table`
            (`id`, `title`, `forwarding_type`, `sender_email`, `recipient_phone`, `recipient_email`, `error_text`)
            VALUES (1, 'Chain v4 Title', 'EMAIL', 'sender@example.com', '+123456789', 'recipient@example.com', 'none')
            """.trimIndent()
        )
        db.close()

        val migratedDb = helper.runMigrationsAndValidate(
            "test_db_1_4",
            4,
            true,
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4
        )

        val forwardingCursor = migratedDb.query("SELECT * FROM forwarding_table WHERE id = 1")
        assertTrue(forwardingCursor.moveToFirst())
        assertEquals("Chain v4 Title", forwardingCursor.getString(forwardingCursor.getColumnIndexOrThrow("title")))
        assertEquals("", forwardingCursor.getString(forwardingCursor.getColumnIndexOrThrow("google_chat_web_hook")))
        forwardingCursor.close()

        migratedDb.execSQL(
            """
            INSERT INTO `processed_messages_table` (`fingerprint`, `created_at`)
            VALUES ('fp-chain', 3000)
            """.trimIndent()
        )
    }
}
