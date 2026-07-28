package com.example.pokedex.domain.sync

import kotlinx.coroutines.flow.Flow

/** Domain boundary for requesting and observing persistent data synchronization. */
interface SyncManager {
    /**
     * Emits `true` while any work carrying the synchronization tag is running.
     */
    val isSyncing: Flow<Boolean>

    /** Enqueues one unique manual synchronization request when no equivalent work is active. */
    fun requestSync()
}
