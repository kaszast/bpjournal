package com.kaszast.bpjournal

import android.app.Application
import com.kaszast.bpjournal.data.BloodPressureDbHelper
import com.kaszast.bpjournal.data.BloodPressureRepository
import com.kaszast.bpjournal.data.BloodPressureRepositoryImpl
import com.kaszast.bpjournal.health.HealthConnectHelper

class BPJournalApplication : Application() {

    lateinit var repository: BloodPressureRepository
        private set

    lateinit var healthConnectHelper: HealthConnectHelper
        private set

    override fun onCreate() {
        super.onCreate()
        val dbHelper = BloodPressureDbHelper(this)
        repository = BloodPressureRepositoryImpl(dbHelper)
        healthConnectHelper = HealthConnectHelper(this)
    }
}
