package com.example.pokedex.domain.remoteconfig

import kotlinx.coroutines.flow.Flow

/**
 * Interface per gestire l'abilitazione delle funzionalità da remoto.
 */
interface FeatureFlagManager {
    /**
     * Sincronizza i flag con il backend remoto (es. all'avvio dell'app).
     */
    suspend fun fetchAndActivate()

    /**
     * Ritorna il valore booleano di un flag come Flow, per aggiornare la UI in realtime.
     */
    fun isFeatureEnabled(flagKey: String): Flow<Boolean>

    /**
     * Ritorna il valore booleano istantaneo di un flag.
     */
    fun isFeatureEnabledSync(flagKey: String): Boolean
}
