package com.kaszast.bpjournal.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.kaszast.bpjournal.model.Arm
import com.kaszast.bpjournal.model.BloodPressureEntry
import com.kaszast.bpjournal.model.BodyPosition

class BloodPressureDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "bpjournal.db"
        const val DATABASE_VERSION = 1

        const val TABLE_NAME = "bp_entries"
        const val COLUMN_ID = "_id"
        const val COLUMN_SYSTOLIC = "systolic"
        const val COLUMN_DIASTOLIC = "diastolic"
        const val COLUMN_PULSE = "pulse"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_ARM = "arm"
        const val COLUMN_POSITION = "position"
        const val COLUMN_TAGS = "tags"
        const val COLUMN_NOTES = "notes"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSql = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SYSTOLIC INTEGER NOT NULL,
                $COLUMN_DIASTOLIC INTEGER NOT NULL,
                $COLUMN_PULSE INTEGER NOT NULL,
                $COLUMN_TIMESTAMP INTEGER NOT NULL,
                $COLUMN_ARM TEXT NOT NULL,
                $COLUMN_POSITION TEXT NOT NULL,
                $COLUMN_TAGS TEXT NOT NULL,
                $COLUMN_NOTES TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableSql)
        db.execSQL("CREATE INDEX idx_timestamp ON $TABLE_NAME ($COLUMN_TIMESTAMP DESC)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Jövőbeli migrációk helye
    }

    fun insert(entry: BloodPressureEntry): Long {
        val values = ContentValues().apply {
            put(COLUMN_SYSTOLIC, entry.systolic)
            put(COLUMN_DIASTOLIC, entry.diastolic)
            put(COLUMN_PULSE, entry.pulse)
            put(COLUMN_TIMESTAMP, entry.timestamp)
            put(COLUMN_ARM, entry.arm.name)
            put(COLUMN_POSITION, entry.position.name)
            put(COLUMN_TAGS, entry.tags.joinToString(","))
            put(COLUMN_NOTES, entry.notes)
        }
        return writableDatabase.insert(TABLE_NAME, null, values)
    }

    fun update(entry: BloodPressureEntry): Int {
        val values = ContentValues().apply {
            put(COLUMN_SYSTOLIC, entry.systolic)
            put(COLUMN_DIASTOLIC, entry.diastolic)
            put(COLUMN_PULSE, entry.pulse)
            put(COLUMN_TIMESTAMP, entry.timestamp)
            put(COLUMN_ARM, entry.arm.name)
            put(COLUMN_POSITION, entry.position.name)
            put(COLUMN_TAGS, entry.tags.joinToString(","))
            put(COLUMN_NOTES, entry.notes)
        }
        return writableDatabase.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(entry.id.toString()))
    }

    fun delete(id: Long): Int {
        return writableDatabase.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun getAllEntries(): List<BloodPressureEntry> {
        val entries = mutableListOf<BloodPressureEntry>()
        val cursor = readableDatabase.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_TIMESTAMP DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                entries.add(cursorToEntry(it))
            }
        }
        return entries
    }

    fun getEntriesInRange(startTime: Long, endTime: Long): List<BloodPressureEntry> {
        val entries = mutableListOf<BloodPressureEntry>()
        val cursor = readableDatabase.query(
            TABLE_NAME,
            null,
            "$COLUMN_TIMESTAMP BETWEEN ? AND ?",
            arrayOf(startTime.toString(), endTime.toString()),
            null,
            null,
            "$COLUMN_TIMESTAMP ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                entries.add(cursorToEntry(it))
            }
        }
        return entries
    }

    private fun cursorToEntry(cursor: Cursor): BloodPressureEntry {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val systolic = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SYSTOLIC))
        val diastolic = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DIASTOLIC))
        val pulse = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PULSE))
        val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
        val armStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARM))
        val posStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POSITION))
        val tagsStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAGS))
        val notes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES))

        val arm = runCatching { Arm.valueOf(armStr) }.getOrDefault(Arm.LEFT)
        val pos = runCatching { BodyPosition.valueOf(posStr) }.getOrDefault(BodyPosition.SITTING)
        val tags = if (tagsStr.isBlank()) emptySet() else tagsStr.split(",").map { it.trim() }.toSet()

        return BloodPressureEntry(
            id = id,
            systolic = systolic,
            diastolic = diastolic,
            pulse = pulse,
            timestamp = timestamp,
            arm = arm,
            position = pos,
            tags = tags,
            notes = notes
        )
    }
}
