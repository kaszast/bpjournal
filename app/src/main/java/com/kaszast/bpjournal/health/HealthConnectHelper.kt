package com.kaszast.bpjournal.health

import android.content.Context
import android.os.Build
import android.util.Log
import com.kaszast.bpjournal.model.Arm
import com.kaszast.bpjournal.model.BloodPressureEntry
import com.kaszast.bpjournal.model.BodyPosition
import java.time.Instant

sealed class HealthSyncResult {
    data object Success : HealthSyncResult()
    data class Error(val message: String) : HealthSyncResult()
    data object NotSupported : HealthSyncResult()
    data object PermissionRequired : HealthSyncResult()
}

class HealthConnectHelper(private val context: Context) {

    companion object {
        private const val TAG = "HealthConnectHelper"
    }

    /**
     * Ellenőrzi, hogy az adott eszközön elérhető-e a Health Connect integráció.
     */
    fun isHealthConnectAvailable(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
    }

    /**
     * Vérnyomás rekord szinkronizálása a rendszer Health Connect / Google Fit tárolójába.
     */
    suspend fun syncBloodPressureRecord(entry: BloodPressureEntry): HealthSyncResult {
        if (!isHealthConnectAvailable()) {
            Log.i(TAG, "Health Connect natív keretrendszer nem érhető el az Android verzión (min API 34)")
            return HealthSyncResult.NotSupported
        }

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val healthConnectManager = context.getSystemService(android.health.connect.HealthConnectManager::class.java)
                if (healthConnectManager == null) {
                    return HealthSyncResult.NotSupported
                }

                val instant = Instant.ofEpochMilli(entry.timestamp)
                val zoneOffset = java.time.ZoneId.systemDefault().rules.getOffset(instant)

                val bodyPositionInt = when (entry.position) {
                    BodyPosition.SITTING -> android.health.connect.datatypes.BloodPressureRecord.BodyPosition.BODY_POSITION_SITTING_DOWN
                    BodyPosition.LYING -> android.health.connect.datatypes.BloodPressureRecord.BodyPosition.BODY_POSITION_LYING_DOWN
                    BodyPosition.STANDING -> android.health.connect.datatypes.BloodPressureRecord.BodyPosition.BODY_POSITION_STANDING_UP
                }

                val armLocationInt = when (entry.arm) {
                    Arm.LEFT -> android.health.connect.datatypes.BloodPressureRecord.BloodPressureMeasurementLocation.BLOOD_PRESSURE_MEASUREMENT_LOCATION_LEFT_UPPER_ARM
                    Arm.RIGHT -> android.health.connect.datatypes.BloodPressureRecord.BloodPressureMeasurementLocation.BLOOD_PRESSURE_MEASUREMENT_LOCATION_RIGHT_UPPER_ARM
                }

                val metadata = android.health.connect.datatypes.Metadata.Builder()
                    .setClientRecordId("bpjournal_${entry.id}_${entry.timestamp}")
                    .build()

                val record = android.health.connect.datatypes.BloodPressureRecord.Builder(
                    metadata,
                    instant,
                    armLocationInt,
                    android.health.connect.datatypes.units.Pressure.fromMillimetersOfMercury(entry.systolic.toDouble()),
                    android.health.connect.datatypes.units.Pressure.fromMillimetersOfMercury(entry.diastolic.toDouble()),
                    bodyPositionInt
                ).setZoneOffset(zoneOffset).build()

                // Rekord mentése Health Connect-be
                kotlinx.coroutines.suspendCancellableCoroutine<HealthSyncResult> { continuation ->
                    healthConnectManager.insertRecords(
                        listOf(record),
                        context.mainExecutor,
                        object : android.os.OutcomeReceiver<android.health.connect.InsertRecordsResponse, android.health.connect.HealthConnectException> {
                            override fun onResult(result: android.health.connect.InsertRecordsResponse?) {
                                Log.i(TAG, "Health Connect azonnali szinkronizáció sikeres!")
                                continuation.resumeWith(Result.success(HealthSyncResult.Success))
                            }

                            override fun onError(error: android.health.connect.HealthConnectException) {
                                Log.e(TAG, "Health Connect hiba: ${error.message}", error)
                                continuation.resumeWith(Result.success(HealthSyncResult.Error(error.message ?: "Ismeretlen hiba")))
                            }
                        }
                    )
                }
            } else {
                HealthSyncResult.NotSupported
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "Health Connect jogosultság hiányzik: ${e.message}")
            HealthSyncResult.PermissionRequired
        } catch (e: Exception) {
            Log.e(TAG, "Váratlan hiba a Health Connect szinkronizálás közben: ${e.message}", e)
            HealthSyncResult.Error(e.message ?: "Szinkronizációs kivétel")
        }
    }
}
