package com.github.smsforwarder

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
}
