package com.example.pokedex.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pokedex.di.DatabaseModule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PokedexDatabaseMigrationTest {
    @get:Rule
    val helper =
        MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            PokedexDatabase::class.java,
        )

    @Test
    fun migrateAllSchemasToLatest() {
        helper.createDatabase(TEST_DATABASE, 1).apply {
            execSQL(
                """
                INSERT INTO pokemon
                    (id, name, imageUrl, types, height, weight, stats)
                VALUES
                    (1, 'bulbasaur', 'image', 'Grass,Poison', 7, 69, 'hp:45')
                """.trimIndent(),
            )
            close()
        }

        helper
            .runMigrationsAndValidate(
                TEST_DATABASE,
                4,
                true,
                DatabaseModule.MIGRATION_1_2,
                DatabaseModule.MIGRATION_2_3,
                DatabaseModule.MIGRATION_3_4,
            ).query("SELECT isFavorite FROM pokemon WHERE id = 1")
            .use { cursor ->
                check(cursor.moveToFirst())
                check(cursor.getInt(0) == 0)
            }
    }

    private companion object {
        const val TEST_DATABASE = "migration-test"
    }
}
