package com.example.pokedex.di

import android.content.Context
import androidx.room.Room
import com.example.pokedex.data.local.PokedexDatabase
import com.example.pokedex.data.local.dao.PokemonDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePokedexDatabase(@ApplicationContext context: Context): PokedexDatabase {
        val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE pokemon ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
            }
        }
        return Room.databaseBuilder(
            context,
            PokedexDatabase::class.java,
            PokedexDatabase.DATABASE_NAME
        )
            .addMigrations(MIGRATION_2_3)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providePokemonDao(database: PokedexDatabase): PokemonDao {
        return database.pokemonDao()
    }
}
