package com.example.pokedex.di

import com.example.pokedex.data.sync.WorkManagerSyncManager
import com.example.pokedex.domain.sync.SyncManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {
    @Binds
    @Singleton
    abstract fun bindSyncManager(syncManagerImpl: WorkManagerSyncManager): SyncManager
}
