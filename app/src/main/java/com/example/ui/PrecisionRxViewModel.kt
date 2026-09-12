package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PrecisionRxDatabase
import com.example.data.model.ActiveMedicationEntity
import com.example.data.model.DiseaseRiskPrediction
import com.example.data.model.EvaluatedInteraction
import com.example.data.model.GeneticMarkerEntity
import com.example.data.model.HealthTrendPointEntity
import com.example.data.model.HipaaAuditLogEntity
import com.example.data.model.PatientProfileEntity
import com.example.data.model.ProviderMessageEntity
import com.example.data.model.RoutineCheckupEntity
import com.example.data.model.SymptomReportEntity
import com.example.data.repository.PrecisionRxRepository
import com.example.service.HipaaSecurityManager
import com.example.service.PrecisionDietProtocol
import com.example.service.PrecisionMedicineEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrecisionRxViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: PrecisionRxRepository

  init {
    val db = PrecisionRxDatabase.getDatabase(application)
    repository = PrecisionRxRepository(db.precisionRxDao())
    viewModelScope.launch {
      repository.prepopulateInitialDataIfEmpty()
    }
  }

  // Security & App Lock State
  private val _isAppLocked = MutableStateFlow(false)
  val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

  private val _securityPin = MutableStateFlow("1234") // default demo PIN
  private val _pinError = MutableStateFlow<String?>(null)
  val pinError: StateFlow<String?> = _pinError.asStateFlow()

  // Consent states
  private val _researchConsentGranted = MutableStateFlow(true)
  val researchConsentGranted: StateFlow<Boolean> = _researchConsentGranted.asStateFlow()

  private val _providerTelemetrySync = MutableStateFlow(true)
  val providerTelemetrySync: StateFlow<Boolean> = _providerTelemetrySync.asStateFlow()

  // Export State
  private val _safeHarborExport = MutableStateFlow<HipaaSecurityManager.AnonymizedExportResult?>(null)
  val safeHarborExport: StateFlow<HipaaSecurityManager.AnonymizedExportResult?> = _safeHarborExport.asStateFlow()

  // Repository Flows
  val patientProfile: StateFlow<PatientProfileEntity?> = repository.patientProfile.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = null
  )

  val geneticMarkers: StateFlow<List<GeneticMarkerEntity>> = repository.geneticMarkers.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  val activeMedications: StateFlow<List<ActiveMedicationEntity>> = repository.medications.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  val symptomReports: StateFlow<List<SymptomReportEntity>> = repository.symptomReports.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  val healthTrendPoints: StateFlow<List<HealthTrendPointEntity>> = repository.healthTrendPoints.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  val checkupAlerts: StateFlow<List<RoutineCheckupEntity>> = repository.checkupAlerts.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  val providerMessages: StateFlow<List<ProviderMessageEntity>> = repository.providerMessages.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  val auditLogs: StateFlow<List<HipaaAuditLogEntity>> = repository.auditLogs.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  // Derived Pharmacogenomic & Drug-Drug Interactions
  val drugInteractions: StateFlow<List<EvaluatedInteraction>> = combine(
    activeMedications,
    geneticMarkers
  ) { meds, markers ->
    PrecisionMedicineEngine.evaluateDrugInteractions(meds, markers)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  // Derived Disease Risk Predictions
  val diseaseRiskPredictions: StateFlow<List<DiseaseRiskPrediction>> = combine(
    geneticMarkers
  ) { (markers) ->
    PrecisionMedicineEngine.calculateDiseaseRiskPredictions(markers)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  // Derived Precision Diet Plans
  val dietProtocols: StateFlow<List<PrecisionDietProtocol>> = combine(
    geneticMarkers
  ) { (markers) ->
    PrecisionMedicineEngine.getPersonalizedDietAndLifestyle(markers)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  // Actions
  fun unlockWithPin(enteredPin: String): Boolean {
    if (enteredPin == _securityPin.value) {
      _isAppLocked.value = false
      _pinError.value = null
      viewModelScope.launch {
        repository.logHipaaAudit("SESSION_AUTHENTICATED", "Biometric/PIN unlock authorized", "DeviceOwner")
      }
      return true
    } else {
      _pinError.value = "Incorrect security PIN. Access denied."
      return false
    }
  }

  fun lockApp() {
    _isAppLocked.value = true
    viewModelScope.launch {
      repository.logHipaaAudit("SESSION_LOCKED", "Manual or timeout lock triggered", "System:AutoLock")
    }
  }

  fun toggleResearchConsent(granted: Boolean) {
    _researchConsentGranted.value = granted
    viewModelScope.launch {
      repository.logHipaaAudit("CONSENT_UPDATED", "Genomic Research Data Sharing: $granted", "User:Patient-42")
    }
  }

  fun toggleProviderSync(enabled: Boolean) {
    _providerTelemetrySync.value = enabled
    viewModelScope.launch {
      repository.logHipaaAudit("CONSENT_UPDATED", "Provider Telemetry Sync: $enabled", "User:Patient-42")
    }
  }

  fun updateDemographicsAndVitals(
    age: Int,
    gender: String,
    weightKg: Float,
    heightCm: Float,
    systolic: Int,
    diastolic: Int,
    glucose: Float,
    cholesterol: Float,
    history: String,
    allergies: String
  ) {
    val current = patientProfile.value ?: return
    val updated = current.copy(
      age = age,
      gender = gender,
      weightKg = weightKg,
      heightCm = heightCm,
      systolic = systolic,
      diastolic = diastolic,
      bloodGlucose = glucose,
      totalCholesterol = cholesterol,
      medicalHistory = history,
      allergies = allergies,
      lastUpdated = System.currentTimeMillis()
    )
    viewModelScope.launch {
      repository.updateProfile(updated)

      // Also append to health trend points
      val sdf = SimpleDateFormat("MMM d", Locale.US)
      val newPoint = HealthTrendPointEntity(
        timestamp = System.currentTimeMillis(),
        dateLabel = sdf.format(Date()),
        systolic = systolic,
        diastolic = diastolic,
        bloodGlucose = glucose,
        weightKg = weightKg,
        cholesterolLdl = cholesterol * 0.6f
      )
      repository.addHealthTrendPoint(newPoint)
    }
  }

  fun addMedication(name: String, dosage: String, frequency: String, reason: String) {
    viewModelScope.launch {
      repository.addMedication(
        ActiveMedicationEntity(
          drugName = name.trim(),
          dosage = dosage.trim(),
          frequency = frequency.trim(),
          reason = reason.trim()
        )
      )
    }
  }

  fun removeMedication(id: Long, name: String) {
    viewModelScope.launch {
      repository.removeMedication(id, name)
    }
  }

  fun runSymptomAssessment(selectedSymptoms: List<String>, onReportReady: (SymptomReportEntity) -> Unit) {
    viewModelScope.launch {
      val report = PrecisionMedicineEngine.screenSymptoms(
        selectedSymptoms = selectedSymptoms,
        patientProfile = patientProfile.value,
        markers = geneticMarkers.value
      )
      val id = repository.saveSymptomReport(report)
      onReportReady(report.copy(id = id))
    }
  }

  fun toggleCheckup(id: Long, completed: Boolean) {
    viewModelScope.launch {
      repository.toggleCheckup(id, completed)
    }
  }

  fun sendProviderMessage(text: String, attachmentTitle: String? = null, attachmentType: String? = null) {
    if (text.isBlank()) return
    viewModelScope.launch {
      val userMsg = ProviderMessageEntity(
        providerId = "dr_sarah_chen",
        providerName = "Dr. Sarah Chen, MD",
        providerRole = "Medical Geneticist & Precision Oncologist",
        text = text.trim(),
        timestamp = System.currentTimeMillis(),
        isFromUser = true,
        isEncrypted = true,
        attachmentTitle = attachmentTitle,
        attachmentType = attachmentType
      )
      repository.sendMessage(userMsg)

      // Automated provider response simulation
      kotlinx.coroutines.delay(1200)
      val autoReply = if (attachmentType != null) {
        "Thank you for transmitting this $attachmentTitle. Our clinical precision board will correlate your latest metrics with your genetic profile and update your care protocol shortly."
      } else {
        "Message received securely under HIPAA end-to-end encryption. Dr. Chen or Nurse Specialist Ross will reply within 2 to 4 hours."
      }

      val replyMsg = ProviderMessageEntity(
        providerId = "dr_sarah_chen",
        providerName = "Dr. Sarah Chen, MD",
        providerRole = "Medical Geneticist & Precision Oncologist",
        text = autoReply,
        timestamp = System.currentTimeMillis(),
        isFromUser = false,
        isEncrypted = true
      )
      repository.sendMessage(replyMsg)
    }
  }

  fun generateSafeHarborExport() {
    viewModelScope.launch {
      val result = HipaaSecurityManager.generateSafeHarborDeIdentifiedExport(
        profile = patientProfile.value,
        markerCount = geneticMarkers.value.size,
        medicationCount = activeMedications.value.size
      )
      _safeHarborExport.value = result
      repository.logHipaaAudit("EXPORT_SAFE_HARBOR", "De-identified record generated (Subject: ${result.anonymizedSubjectId})", "User:Patient-42")
    }
  }

  fun clearExport() {
    _safeHarborExport.value = null
  }
}
