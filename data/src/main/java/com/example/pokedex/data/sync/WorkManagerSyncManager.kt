package com.example.pokedex.data.sync

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.pokedex.domain.sync.SyncManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerSyncManager @Inject constructor(
    @ApplicationContext private val context: Context
) : SyncManager {

    private val workManager = WorkManager.getInstance(context)

    override val isSyncing: Flow<Boolean> = workManager
        .getWorkInfosForUniqueWorkFlow(SyncWorker.WORK_NAME)
        .map { workInfos ->
            workInfos.any { it.state == WorkInfo.State.RUNNING }
        }

    override fun requestSync() {
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .build()

        workManager.enqueueUniqueWork(
            SyncWorker.WORK_NAME + "_manual",
            ExistingWorkPolicy.KEEP,
            syncRequest
        )
    }
}
