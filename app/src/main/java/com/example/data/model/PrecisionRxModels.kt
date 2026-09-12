package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Locale

@Entity(tableName = "patient_profile")
data class PatientProfileEntity(
  @PrimaryKey val id: Int = 1,
  val fullName: String,
  val age: Int,
  val gender: String,
  val weightKg: Float,
  val heightCm: Float,
  val bloodType: String = "O+",
  val medicalHistory: String = "",
  val allergies: String = "",
  val surgeries: String = "",
  val familyHistory: String = "",
  val systolic: Int = 120,
  val diastolic: Int = 80,
  val heartRate: Int = 72,
  val bloodGlucose: Float = 95f,
  val totalCholesterol: Float = 190f,
  val lastUpdated: Long = System.currentTimeMillis()
) {
  val bmi: Float
    get() {
      val heightM = heightCm / 100f
      return if (heightM > 0f) weightKg / (heightM * heightM) else 0f
    }

  val bmiCategory: String
    get() = when {
      bmi < 18.5f -> "Underweight"
      bmi < 25.0f -> "Optimal / Normal"
      bmi < 30.0f -> "Overweight"
      else -> "Obese"
    }

  val formattedBmi: String
    get() = String.format(Locale.US, "%.1f", bmi)
}

@Entity(tableName = "genetic_markers")
data class GeneticMarkerEntity(
  @PrimaryKey val id: String = "",
  val gene: String,
  val rsId: String,
  val genotype: String,
  val chromosome: String,
  val phenotype: String,
  val clinicalSignificance: String, // e.g. "Pathogenic", "Poor Metabolizer", "Increased Risk", "Normal"
  val category: String, // "Pharmacogenomics", "Oncology Risk", "Cardiovascular", "Metabolic & Chronic"
  val summary: String,
  val actionableRecommendations: String,
  val cpicOrAcmgLevel: String
)

data class DiseaseRiskPrediction(
  val conditionName: String,
  val category: String, // "Cancer Risk", "Cardiovascular", "Metabolic", "Neurodegenerative"
  val relativeRiskMultiplier: Float, // e.g. 3.4x
  val lifetimeRiskPercent: Int,
  val averagePopulationRiskPercent: Int,
  val riskLevel: String, // "HIGH RISK", "ELEVATED", "AVERAGE", "LOW"
  val keyMarkers: String,
  val preventativeLifestyle: List<String>,
  val recommendedSurveillance: List<String>
)

@Entity(tableName = "active_medications")
data class ActiveMedicationEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val drugName: String,
  val dosage: String,
  val frequency: String,
  val reason: String,
  val addedTimestamp: Long = System.currentTimeMillis()
)

data class EvaluatedInteraction(
  val drugName: String,
  val interactingGeneOrDrug: String,
  val severity: SeverityLevel,
  val type: InteractionType,
  val biologicalMechanism: String,
  val clinicalGuidance: String,
  val recommendedAlternatives: List<String>
) {
  enum class SeverityLevel { CONTRAINDICATED, SEVERE, MODERATE, SAFE }
  enum class InteractionType { GENE_DRUG, DRUG_DRUG }
}

@Entity(tableName = "symptom_reports")
data class SymptomReportEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val timestamp: Long = System.currentTimeMillis(),
  val symptomsReported: String, // joined string
  val suspectedCategory: String, // "Cancer Red Flags", "Viral Fever", "Bacterial Infection", "Chronic Onset"
  val triageLevel: String, // "EMERGENCY", "URGENT_EVALUATION", "ROUTINE_OUTPATIENT"
  val preliminaryDiagnosis: String,
  val clinicalFindings: String,
  val recommendedFollowUpTests: String, // comma separated
  val genomicCorrelationNote: String
)

@Entity(tableName = "health_trend_points")
data class HealthTrendPointEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val timestamp: Long,
  val dateLabel: String,
  val systolic: Int,
  val diastolic: Int,
  val bloodGlucose: Float,
  val weightKg: Float,
  val cholesterolLdl: Float
)

@Entity(tableName = "routine_checkups")
data class RoutineCheckupEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val category: String,
  val dueDate: String,
  val rationale: String,
  val urgency: String, // "High", "Medium", "Routine"
  val isCompleted: Boolean = false
)

@Entity(tableName = "provider_messages")
data class ProviderMessageEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val providerId: String,
  val providerName: String,
  val providerRole: String,
  val text: String,
  val timestamp: Long = System.currentTimeMillis(),
  val isFromUser: Boolean,
  val isEncrypted: Boolean = true,
  val attachmentTitle: String? = null,
  val attachmentType: String? = null
)

@Entity(tableName = "hipaa_audit_logs")
data class HipaaAuditLogEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val timestamp: Long = System.currentTimeMillis(),
  val action: String, // e.g. "VIEW_PHI", "GENERATE_DIAGNOSTIC_REPORT", "EXPORT_SAFE_HARBOR", "DE-IDENTIFY_RECORD"
  val targetResource: String,
  val operator: String,
  val securityHash: String
)
