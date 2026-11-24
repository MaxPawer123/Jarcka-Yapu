package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.model.CostEntryEntity
import com.example.model.Crop
import com.example.model.Diagnosis
import com.example.model.ExpenseRecord
import com.example.model.JarkaRoomConverters
import com.example.model.Pest
import com.example.model.SavedDiagnosisEntity
import com.example.model.Treatment
import com.example.model.WeatherAlert

@Database(
    entities = [
        Crop::class,
        Pest::class,
        Treatment::class,
        Diagnosis::class,
        ExpenseRecord::class,
        WeatherAlert::class,
        SavedDiagnosisEntity::class,
        CostEntryEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(JarkaRoomConverters::class)
abstract class JarkaYapuDatabase : RoomDatabase() {
    abstract fun jarkaYapuDao(): JarkaYapuDao
    abstract fun savedDiagnosisDao(): SavedDiagnosisDao
    abstract fun costEntryDao(): CostEntryDao

    companion object {
        @Volatile
        private var INSTANCE: JarkaYapuDatabase? = null

        fun getInstance(context: Context): JarkaYapuDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    JarkaYapuDatabase::class.java,
                    "jarka_yapu_offline.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
