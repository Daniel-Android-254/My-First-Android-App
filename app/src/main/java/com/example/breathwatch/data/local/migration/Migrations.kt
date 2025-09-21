package com.example.breathwatch.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create the cat_facts table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS cat_facts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    fact TEXT NOT NULL,
                    timestamp INTEGER NOT NULL
                )
            """)

            // Add timestamp columns to existing tables if not present
            database.execSQL("""
                ALTER TABLE air_quality_table
                ADD COLUMN timestamp INTEGER NOT NULL DEFAULT 0
            """)
            database.execSQL("""
                ALTER TABLE weather_table
                ADD COLUMN timestamp INTEGER NOT NULL DEFAULT 0
            """)
        }
    }

    // Add future migrations here as the schema evolves
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Future migrations will go here
        }
    }

    fun getAllMigrations(): Array<Migration> {
        return arrayOf(MIGRATION_1_2, MIGRATION_2_3)
    }
}
