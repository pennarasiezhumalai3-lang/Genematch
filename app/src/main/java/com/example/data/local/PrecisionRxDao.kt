package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ActiveMedicationEntity
import com.example.data.model.GeneticMarkerEntity
import com.example.data.model.HealthTrendPointEntity
import com.example.data.model.HipaaAuditLogEntity
import com.example.data.model.PatientProfileEntity
import com.example.data.model.ProviderMessageEntity
import com.example.data.model.RoutineCheckupEntity
import com.example.data.model.SymptomReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrecisionRxDao {

  // Patient Profile
  @Query("SELECT * FROM patient_profile WHERE id = 1 LIMIT 1")
  fun getPatientProfile(): Flow<PatientProfileEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateProfile(profile: PatientProfileEntity)

  // Genetic Markers
  @Query("SELECT * FROM genetic_markers ORDER BY category ASC, gene ASC")
  fun getAllGeneticMarkers(): Flow<List<GeneticMarkerEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGeneticMarkers(markers: List<GeneticMarkerEntity>)

  // Medications
  @Query("SELECT * FROM active_medications ORDER BY addedTimestamp DESC")
  fun getAllMedications(): Flow<List<ActiveMedicationEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMedication(med: ActiveMedicationEntity): Long

  @Query("DELETE FROM active_medications WHERE id = :id")
  suspend fun deleteMedication(id: Long)

  // Symptom Reports
  @Query("SELECT * FROM symptom_reports ORDER BY timestamp DESC")
  fun getAllSymptomReports(): Flow<List<SymptomReportEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSymptomReport(report: SymptomReportEntity): Long

  // Health Trend Points
  @Query("SELECT * FROM health_trend_points ORDER BY timestamp ASC")
  fun getHealthTrendPoints(): Flow<List<HealthTrendPointEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHealthTrendPoints(points: List<HealthTrendPointEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSingleTrendPoint(point: HealthTrendPointEntity): Long

  // Routine Checkups
  @Query("SELECT * FROM routine_checkups ORDER BY isCompleted ASC, id ASC")
  fun getAllCheckups(): Flow<List<RoutineCheckupEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCheckups(checkups: List<RoutineCheckupEntity>)

  @Query("UPDATE routine_checkups SET isCompleted = :completed WHERE id = :id")
  suspend fun updateCheckupStatus(id: Long, completed: Boolean)

  // Provider Messages
  @Query("SELECT * FROM provider_messages ORDER BY timestamp ASC")
  fun getAllMessages(): Flow<List<ProviderMessageEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessage(msg: ProviderMessageEntity): Long

  // HIPAA Audit Logs
  @Query("SELECT * FROM hipaa_audit_logs ORDER BY timestamp DESC")
  fun getAllAuditLogs(): Flow<List<HipaaAuditLogEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAuditLog(log: HipaaAuditLogEntity): Long
}
