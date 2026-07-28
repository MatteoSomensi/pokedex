package com.example.pokedex.domain.sync

import kotlinx.coroutines.flow.Flow

/**
 * Interface representing the current state of a sync operation.
 */
interface SyncManager {
    /**
     * A flow that emits true when a synchronization process is currently running,
     * and false otherwise.
     */
    val isSyncing: Flow<Boolean>

    /**
     * Triggers a manual synchronization process.
     */
    fun requestSync()
}
