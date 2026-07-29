package com.example.pokedex.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.pokedex.domain.sync.SyncManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WorkManager-backed [SyncManager] implementation.
 *
 * Manual requests are unique and use [ExistingWorkPolicy.KEEP], so repeated UI actions do not
 * enqueue duplicate synchronization work. The request is deferred until a network is available.
 */
@Singleton
class WorkManagerSyncManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : SyncManager {
        private val workManager = WorkManager.getInstance(context)

        override val isSyncing: Flow<Boolean> =
            workManager
                .getWorkInfosByTagFlow(SyncWorker.SYNC_TAG)
                .map { workInfos ->
                    workInfos.any { it.state == WorkInfo.State.RUNNING }
                }

        override fun requestSync() {
            val constraints =
                Constraints
                    .Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            val syncRequest =
                OneTimeWorkRequestBuilder<SyncWorker>()
                    .setConstraints(constraints)
                    .addTag(SyncWorker.SYNC_TAG)
                    .build()

            workManager.enqueueUniqueWork(
                SyncWorker.MANUAL_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                syncRequest,
            )
        }
    }
