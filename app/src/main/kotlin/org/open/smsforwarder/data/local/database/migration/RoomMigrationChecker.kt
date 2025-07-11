package org.open.smsforwarder.data.local.database.migration

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.migration.Migration
import org.open.smsforwarder.data.local.database.AppDatabase

object RoomMigrationChecker {

    fun validateMigrations(
        context: Context,
        registeredMigrations: List<Migration>
    ) {
        val targetVersion = AppDatabase.DATABASE_VERSION
        val dbVersion = getVersion(context)
        if (dbVersion == targetVersion || dbVersion == -1) {
            return // No migration needed: DB is already up to date or doesn't exists yet
        }

        val expectedMigrations = mutableSetOf<Int>()
        for (version in dbVersion until targetVersion) {
            expectedMigrations += version
        }

        val actualMigrations = registeredMigrations.map { it.startVersion }.toSet()
        val missingMigrations = expectedMigrations - actualMigrations

        if (missingMigrations.isNotEmpty()) {
            error(
                "❌ Room migration(s) missing from version(s): $missingMigrations\n" +
                        "Detected DB version: $dbVersion, Target DB version: $targetVersion.\n" +
                        "You must addMigration(s)."
            )
        } else {
            println("✅ All required Room migrations are registered.")
        }
    }

    private fun getVersion(context: Context): Int {
        val dbPath = context.getDatabasePath(AppDatabase.DATABASE_NAME)
        if (!dbPath.exists()) return -1

        val db = SQLiteDatabase.openDatabase(
            dbPath.absolutePath,
            null,
            SQLiteDatabase.OPEN_READONLY
        )

        val cursor = db.rawQuery("PRAGMA user_version", null)
        val version = if (cursor.moveToFirst()) cursor.getInt(0) else -1

        cursor.close()
        db.close()

        return version
    }
}
