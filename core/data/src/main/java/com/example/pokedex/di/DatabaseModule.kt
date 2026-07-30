package com.example.pokedex.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.pokedex.data.local.PokedexDatabase
import com.example.pokedex.data.local.dao.PokemonDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Provides the Room database, its migrations, and data access objects as app-wide singletons. */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providePokedexDatabase(
        @ApplicationContext context: Context,
    ): PokedexDatabase =
        Room
            .databaseBuilder(
                context,
                PokedexDatabase::class.java,
                PokedexDatabase.DATABASE_NAME,
            ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .build()

    @Provides
    @Singleton
    fun providePokemonDao(database: PokedexDatabase): PokemonDao = database.pokemonDao()

    internal val MIGRATION_1_2 =
        object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `types` (`name` TEXT NOT NULL, PRIMARY KEY(`name`))",
                )
            }
        }

    internal val MIGRATION_2_3 =
        object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE `pokemon` ADD COLUMN `isFavorite` INTEGER NOT NULL DEFAULT 0",
                )
            }
        }

    internal val MIGRATION_3_4 =
        object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `pokemon_remote_keys` (
                        `pokemonId` INTEGER NOT NULL,
                        `query` TEXT NOT NULL,
                        `prevOffset` INTEGER,
                        `nextOffset` INTEGER,
                        PRIMARY KEY(`pokemonId`, `query`)
                    )
                    """.trimIndent(),
                )
            }
        }
}
