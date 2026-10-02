package com.kaszast.bpjournal.data

import com.kaszast.bpjournal.model.BloodPressureEntry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

/**
 * Repository interface providing reactive data access and mutations for blood pressure records.
 *
 * Exposes thread-safe [Flow] streams that automatically emit fresh data whenever
 * underlying records are inserted, updated, or deleted.
 */
interface BloodPressureRepository {
    fun getAllEntries(): Flow<List<BloodPressureEntry>>
    fun getEntriesInRange(startTime: Long, endTime: Long): Flow<List<BloodPressureEntry>>
    suspend fun insertEntry(entry: BloodPressureEntry): Long
    suspend fun updateEntry(entry: BloodPressureEntry): Boolean
    suspend fun deleteEntry(id: Long): Boolean
    suspend fun deleteAllEntries(): Boolean
    suspend fun getAllEntriesSync(): List<BloodPressureEntry>
}

/**
 * Concrete repository implementation backed by local SQLite via [BloodPressureDbHelper].
 *
 * Uses an internal [MutableSharedFlow] trigger to signal data mutations and re-emit updated
 * lists across active [Flow] observers, ensuring IO work remains confined to [Dispatchers.IO].
 */
class BloodPressureRepositoryImpl(
    private val dbHelper: BloodPressureDbHelper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BloodPressureRepository {

    private val dataUpdateTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    override fun getAllEntries(): Flow<List<BloodPressureEntry>> = flow {
        emit(dbHelper.getAllEntries())
        dataUpdateTrigger.collect {
            emit(dbHelper.getAllEntries())
        }
    }.flowOn(ioDispatcher)

    override fun getEntriesInRange(startTime: Long, endTime: Long): Flow<List<BloodPressureEntry>> = flow {
        emit(dbHelper.getEntriesInRange(startTime, endTime))
        dataUpdateTrigger.collect {
            emit(dbHelper.getEntriesInRange(startTime, endTime))
        }
    }.flowOn(ioDispatcher)

    override suspend fun getAllEntriesSync(): List<BloodPressureEntry> = withContext(ioDispatcher) {
        dbHelper.getAllEntries()
    }

    override suspend fun insertEntry(entry: BloodPressureEntry): Long = withContext(ioDispatcher) {
        val id = dbHelper.insert(entry)
        dataUpdateTrigger.tryEmit(Unit)
        id
    }

    override suspend fun updateEntry(entry: BloodPressureEntry): Boolean = withContext(ioDispatcher) {
        val count = dbHelper.update(entry)
        dataUpdateTrigger.tryEmit(Unit)
        count > 0
    }

    override suspend fun deleteEntry(id: Long): Boolean = withContext(ioDispatcher) {
        val count = dbHelper.delete(id)
        dataUpdateTrigger.tryEmit(Unit)
        count > 0
    }

    override suspend fun deleteAllEntries(): Boolean = withContext(ioDispatcher) {
        val count = dbHelper.deleteAll()
        dataUpdateTrigger.tryEmit(Unit)
        count > 0
    }
}
