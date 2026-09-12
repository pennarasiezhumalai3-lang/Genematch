package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ActiveMedicationEntity
import com.example.data.model.GeneticMarkerEntity
import com.example.data.model.HealthTrendPointEntity
import com.example.data.model.HipaaAuditLogEntity
import com.example.data.model.PatientProfileEntity
import com.example.data.model.ProviderMessageEntity
import com.example.data.model.RoutineCheckupEntity
import com.example.data.model.SymptomReportEntity

@Database(
  entities = [
    PatientProfileEntity::class,
    GeneticMarkerEntity::class,
    ActiveMedicationEntity::class,
    SymptomReportEntity::class,
    HealthTrendPointEntity::class,
    RoutineCheckupEntity::class,
    ProviderMessageEntity::class,
    HipaaAuditLogEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class PrecisionRxDatabase : RoomDatabase() {
  abstract fun precisionRxDao(): PrecisionRxDao

  companion object {
    @Volatile
    private var INSTANCE: PrecisionRxDatabase? = null

    fun getDatabase(context: Context): PrecisionRxDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          PrecisionRxDatabase::class.java,
          "precision_rx_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
