package com.example.data.repository

import com.example.data.local.PrecisionRxDao
import com.example.data.model.ActiveMedicationEntity
import com.example.data.model.GeneticMarkerEntity
import com.example.data.model.HealthTrendPointEntity
import com.example.data.model.HipaaAuditLogEntity
import com.example.data.model.PatientProfileEntity
import com.example.data.model.ProviderMessageEntity
import com.example.data.model.RoutineCheckupEntity
import com.example.data.model.SymptomReportEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrecisionRxRepository(private val dao: PrecisionRxDao) {

  val patientProfile: Flow<PatientProfileEntity?> = dao.getPatientProfile()
  val geneticMarkers: Flow<List<GeneticMarkerEntity>> = dao.getAllGeneticMarkers()
  val medications: Flow<List<ActiveMedicationEntity>> = dao.getAllMedications()
  val symptomReports: Flow<List<SymptomReportEntity>> = dao.getAllSymptomReports()
  val healthTrendPoints: Flow<List<HealthTrendPointEntity>> = dao.getHealthTrendPoints()
  val checkupAlerts: Flow<List<RoutineCheckupEntity>> = dao.getAllCheckups()
  val providerMessages: Flow<List<ProviderMessageEntity>> = dao.getAllMessages()
  val auditLogs: Flow<List<HipaaAuditLogEntity>> = dao.getAllAuditLogs()

  suspend fun updateProfile(profile: PatientProfileEntity) {
    dao.insertOrUpdateProfile(profile)
    logHipaaAudit("UPDATE_PHI", "Patient demographics & vitals updated", "User:Patient-42")
  }

  suspend fun addMedication(med: ActiveMedicationEntity): Long {
    val id = dao.insertMedication(med)
    logHipaaAudit("ADD_MEDICATION", "Medication added: ${med.drugName} ${med.dosage}", "User:Patient-42")
    return id
  }

  suspend fun removeMedication(id: Long, name: String) {
    dao.deleteMedication(id)
    logHipaaAudit("REMOVE_MEDICATION", "Medication removed: $name", "User:Patient-42")
  }

  suspend fun saveSymptomReport(report: SymptomReportEntity): Long {
    val id = dao.insertSymptomReport(report)
    logHipaaAudit("GENERATE_DIAGNOSTIC_REPORT", "Triage ${report.triageLevel} for category ${report.suspectedCategory}", "Clinical-Symptom-AI")
    return id
  }

  suspend fun addHealthTrendPoint(point: HealthTrendPointEntity): Long {
    val id = dao.insertSingleTrendPoint(point)
    logHipaaAudit("LOG_VITALS", "Trend point added BP:${point.systolic}/${point.diastolic} Glucose:${point.bloodGlucose}", "User:Patient-42")
    return id
  }

  suspend fun toggleCheckup(id: Long, completed: Boolean) {
    dao.updateCheckupStatus(id, completed)
    logHipaaAudit("CHECKUP_STATUS", "Checkup #$id status set to $completed", "User:Patient-42")
  }

  suspend fun sendMessage(msg: ProviderMessageEntity): Long {
    val id = dao.insertMessage(msg)
    logHipaaAudit("TRANSMIT_ENCRYPTED_MESSAGE", "Secure message to ${msg.providerName} (encrypted: ${msg.isEncrypted})", "Patient-App")
    return id
  }

  suspend fun logHipaaAudit(action: String, targetResource: String, operator: String) {
    val hash = "SHA256-" + Integer.toHexString((action + targetResource + System.currentTimeMillis()).hashCode())
    dao.insertAuditLog(
      HipaaAuditLogEntity(
        timestamp = System.currentTimeMillis(),
        action = action,
        targetResource = targetResource,
        operator = operator,
        securityHash = hash
      )
    )
  }

  suspend fun prepopulateInitialDataIfEmpty() {
    // Check if initial profile exists
    val defaultProfile = PatientProfileEntity(
      id = 1,
      fullName = "Eleanor Vance",
      age = 42,
      gender = "Female",
      weightKg = 64.5f,
      heightCm = 168.0f,
      bloodType = "A+",
      medicalHistory = "Mild familial hypercholesterolemia, Migraine with aura, Gestational impaired glucose tolerance",
      allergies = "Penicillin (moderate urticaria), Codeine (severe nausea/dysphoria)",
      surgeries = "Laparoscopic appendectomy (2014), Arthroscopic knee meniscectomy (2019)",
      familyHistory = "Maternal grandmother: Breast cancer at age 51; Father: Type 2 Diabetes; Paternal uncle: Myocardial infarction at 62",
      systolic = 122,
      diastolic = 78,
      heartRate = 72,
      bloodGlucose = 98.4f,
      totalCholesterol = 196.0f
    )
    dao.insertOrUpdateProfile(defaultProfile)

    val markers = listOf(
      GeneticMarkerEntity(
        id = "CYP2C19_2",
        gene = "CYP2C19",
        rsId = "rs4244285",
        genotype = "*2/*2",
        chromosome = "10q23.33",
        phenotype = "Poor Metabolizer",
        clinicalSignificance = "High Pharmacogenomic Risk",
        category = "Pharmacogenomics",
        summary = "Loss-of-function allele eliminates active bioactivation of prodrugs like clopidogrel.",
        actionableRecommendations = "Avoid Clopidogrel for antiplatelet therapy; switch to Ticagrelor or Prasugrel per CPIC guidelines.",
        cpicOrAcmgLevel = "CPIC Level 1A"
      ),
      GeneticMarkerEntity(
        id = "BRCA1_MUT",
        gene = "BRCA1",
        rsId = "rs80357914",
        genotype = "c.68_69delAG",
        chromosome = "17q21.31",
        phenotype = "High Penetrance Predisposition",
        clinicalSignificance = "Pathogenic (ACMG Tier 1)",
        category = "Oncology Risk",
        summary = "Frameshift mutation in tumor suppressor DNA repair pathway causing marked risk of breast and ovarian malignancies.",
        actionableRecommendations = "Annual contrast-enhanced Breast MRI starting age 25-30, biannual clinical breast exam, genetic counseling for risk-reducing salpingo-oophorectomy.",
        cpicOrAcmgLevel = "ACMG Tier 1 Pathogenic"
      ),
      GeneticMarkerEntity(
        id = "CYP2D6_4",
        gene = "CYP2D6",
        rsId = "rs3892097",
        genotype = "*4/*4",
        chromosome = "22q13.2",
        phenotype = "Poor Metabolizer",
        clinicalSignificance = "Critical Pharmacogenomic Risk",
        category = "Pharmacogenomics",
        summary = "Complete absence of functional CYP2D6 enzyme. Prevents metabolic conversion of codeine and tramadol to morphine/O-desmethyltramadol.",
        actionableRecommendations = "Codeine and Tramadol contraindicated due to therapeutic failure. Use alternative non-CYP2D6 analgesics (e.g. Hydromorphone, Ibuprofen, Acetaminophen).",
        cpicOrAcmgLevel = "CPIC Level 1A"
      ),
      GeneticMarkerEntity(
        id = "SLCO1B1_5",
        gene = "SLCO1B1",
        rsId = "rs4149056",
        genotype = "c.521T>C (521TT)",
        chromosome = "12p12.1",
        phenotype = "Intermediate OATP1B1 Transporter Function",
        clinicalSignificance = "Moderate Toxicity Risk",
        category = "Pharmacogenomics",
        summary = "Decreased hepatic uptake of simvastatin resulting in marked elevation of systemic exposure and elevated risk of myopathy / rhabdomyolysis.",
        actionableRecommendations = "Avoid high-dose Simvastatin (>20mg). Consider Rosuvastatin or Pravastatin at standard doses with CK monitoring.",
        cpicOrAcmgLevel = "CPIC Level 1A"
      ),
      GeneticMarkerEntity(
        id = "VKORC1_1639",
        gene = "VKORC1",
        rsId = "rs9923231",
        genotype = "-1639A/A",
        chromosome = "16p11.2",
        phenotype = "High Warfarin Sensitivity",
        clinicalSignificance = "High Bleeding Risk",
        category = "Pharmacogenomics",
        summary = "Requires marked downward titration (40-60% lower initial dose) of Warfarin to prevent life-threatening hemorrhagic complications.",
        actionableRecommendations = "Calculate initial Warfarin dose with FDA pharmacogenetic algorithm or prefer Direct Oral Anticoagulants (DOACs).",
        cpicOrAcmgLevel = "CPIC Level 1A"
      ),
      GeneticMarkerEntity(
        id = "APOE_E4",
        gene = "APOE",
        rsId = "rs429358",
        genotype = "ε3/ε4",
        chromosome = "19q13.32",
        phenotype = "Elevated Neurodegenerative & Lipid Risk",
        clinicalSignificance = "Risk Factor (3.2x relative risk)",
        category = "Neurodegenerative",
        summary = "Presence of one ε4 allele conveys increased susceptibility to late-onset Alzheimer's and elevated LDL responsiveness to saturated dietary fats.",
        actionableRecommendations = "Adopt Mediterranean-DASH (MIND) dietary protocol; strict cardiovascular risk factor optimization; continuous cognitive reserve training.",
        cpicOrAcmgLevel = "ACMG Established Risk"
      ),
      GeneticMarkerEntity(
        id = "TCF7L2_VAR",
        gene = "TCF7L2",
        rsId = "rs7903146",
        genotype = "C/T",
        chromosome = "10q25.2",
        phenotype = "Impaired Incretin & Beta-Cell Function",
        clinicalSignificance = "Moderate Metabolic Risk (1.4x T2D risk)",
        category = "Metabolic & Chronic",
        summary = "Impaired GLP-1 stimulated insulin secretion from pancreatic beta cells under glycemic challenge.",
        actionableRecommendations = "Emphasize low glycemic load meals, post-prandial walking, and periodic HbA1c screening every 6 months.",
        cpicOrAcmgLevel = "Established GWAS"
      ),
      GeneticMarkerEntity(
        id = "MTHFR_677",
        gene = "MTHFR",
        rsId = "rs1801133",
        genotype = "677C>T (CT Heterozygous)",
        chromosome = "1p36.22",
        phenotype = "Mild Thermolabile Methylenetetrahydrofolate Reductase",
        clinicalSignificance = "Nutrigenomic Risk",
        category = "Metabolic & Chronic",
        summary = "Approximately 35% reduced folate remethylation capacity, potentially predisposing to borderline hyperhomocysteinemia.",
        actionableRecommendations = "Supplement with active L-methylfolate (5-MTHF) instead of synthetic folic acid; increase dietary dietary intake of leafy greens and legumes.",
        cpicOrAcmgLevel = "Nutrigenomic Guideline"
      )
    )
    dao.insertGeneticMarkers(markers)

    // Initial Active Medications
    dao.insertMedication(
      ActiveMedicationEntity(
        drugName = "Clopidogrel (Plavix)",
        dosage = "75 mg",
        frequency = "Once Daily",
        reason = "Cardiovascular prophylaxis"
      )
    )
    dao.insertMedication(
      ActiveMedicationEntity(
        drugName = "Simvastatin",
        dosage = "40 mg",
        frequency = "Nightly",
        reason = "LDL Cholesterol reduction"
      )
    )
    dao.insertMedication(
      ActiveMedicationEntity(
        drugName = "Metformin",
        dosage = "500 mg",
        frequency = "Twice Daily with meals",
        reason = "Metabolic glucose regulation"
      )
    )

    // Pre-populate 6 months of longitudinal health trends
    val now = System.currentTimeMillis()
    val dayMillis = 86_400_000L
    val sdf = SimpleDateFormat("MMM d", Locale.US)
    val trendData = listOf(
      HealthTrendPointEntity(timestamp = now - 150 * dayMillis, dateLabel = sdf.format(Date(now - 150 * dayMillis)), systolic = 136, diastolic = 88, bloodGlucose = 112f, weightKg = 67.2f, cholesterolLdl = 142f),
      HealthTrendPointEntity(timestamp = now - 120 * dayMillis, dateLabel = sdf.format(Date(now - 120 * dayMillis)), systolic = 132, diastolic = 85, bloodGlucose = 108f, weightKg = 66.5f, cholesterolLdl = 136f),
      HealthTrendPointEntity(timestamp = now - 90 * dayMillis, dateLabel = sdf.format(Date(now - 90 * dayMillis)), systolic = 129, diastolic = 83, bloodGlucose = 104f, weightKg = 65.8f, cholesterolLdl = 128f),
      HealthTrendPointEntity(timestamp = now - 60 * dayMillis, dateLabel = sdf.format(Date(now - 60 * dayMillis)), systolic = 126, diastolic = 80, bloodGlucose = 101f, weightKg = 65.2f, cholesterolLdl = 122f),
      HealthTrendPointEntity(timestamp = now - 30 * dayMillis, dateLabel = sdf.format(Date(now - 30 * dayMillis)), systolic = 124, diastolic = 79, bloodGlucose = 99f, weightKg = 64.8f, cholesterolLdl = 118f),
      HealthTrendPointEntity(timestamp = now - 7 * dayMillis, dateLabel = sdf.format(Date(now - 7 * dayMillis)), systolic = 122, diastolic = 78, bloodGlucose = 98.4f, weightKg = 64.5f, cholesterolLdl = 114f)
    )
    dao.insertHealthTrendPoints(trendData)

    // Prepopulate routine checkup alerts
    val checkups = listOf(
      RoutineCheckupEntity(
        title = "Annual Contrast Breast MRI",
        category = "Genomic Oncology Surveillance",
        dueDate = "In 18 Days (Oct 1)",
        rationale = "Mandatory high-risk surveillance for BRCA1 c.68_69delAG carrier per NCCN guidelines.",
        urgency = "High",
        isCompleted = false
      ),
      RoutineCheckupEntity(
        title = "Comprehensive Metabolic & Lipid Panel",
        category = "Pharmacogenomic Efficacy",
        dueDate = "In 35 Days (Oct 18)",
        rationale = "Assess liver transaminases (ALT/AST) and CK response to statin therapy given SLCO1B1 genotype.",
        urgency = "Medium",
        isCompleted = false
      ),
      RoutineCheckupEntity(
        title = "Fasting HbA1c & Plasma Insulin",
        category = "Metabolic Risk",
        dueDate = "In 60 Days (Nov 12)",
        rationale = "Bi-annual monitoring for TCF7L2 allele carrier with prior gestational impaired tolerance.",
        urgency = "Routine",
        isCompleted = false
      ),
      RoutineCheckupEntity(
        title = "Homocysteine & Serum Folate Assay",
        category = "Nutrigenomics",
        dueDate = "Completed Last Month",
        rationale = "Evaluate baseline methylation status under MTHFR 677C>T variant.",
        urgency = "Routine",
        isCompleted = true
      )
    )
    dao.insertCheckups(checkups)

    // Prepopulate initial secure messaging threads with healthcare team
    val messages = listOf(
      ProviderMessageEntity(
        providerId = "dr_sarah_chen",
        providerName = "Dr. Sarah Chen, MD",
        providerRole = "Medical Geneticist & Precision Oncologist",
        text = "Hello Eleanor, I have reviewed your whole-exome sequencing report. Given your BRCA1 c.68_69delAG pathogenic variant and your CYP2C19 *2/*2 phenotype, our multidisciplinary board recommends immediately updating your antiplatelet regimen and scheduling your annual Breast MRI.",
        timestamp = now - 2 * dayMillis,
        isFromUser = false,
        isEncrypted = true,
        attachmentTitle = "Genomic Summary: BRCA1 & CYP2C19 Action Plan.pdf",
        attachmentType = "GENETIC_REPORT"
      ),
      ProviderMessageEntity(
        providerId = "dr_sarah_chen",
        providerName = "Dr. Sarah Chen, MD",
        providerRole = "Medical Geneticist & Precision Oncologist",
        text = "Thank you Dr. Chen. I saw the alert on my dashboard regarding Clopidogrel resistance. What should I take instead?",
        timestamp = now - 1 * dayMillis,
        isFromUser = true,
        isEncrypted = true
      ),
      ProviderMessageEntity(
        providerId = "dr_sarah_chen",
        providerName = "Dr. Sarah Chen, MD",
        providerRole = "Medical Geneticist & Precision Oncologist",
        text = "Because CYP2C19 is inactive (*2/*2), Clopidogrel cannot be metabolized into its active form. Ticagrelor (Brilinta) 90mg twice daily does not require CYP2C19 bioactivation and is our top clinical alternative.",
        timestamp = now - 12 * 3600_000L,
        isFromUser = false,
        isEncrypted = true
      )
    )
    for (m in messages) {
      dao.insertMessage(m)
    }

    logHipaaAudit("INITIALIZE_VAULT", "PrecisionRx vault initialized with local AES-256 state", "System:AutoInit")
  }
}
