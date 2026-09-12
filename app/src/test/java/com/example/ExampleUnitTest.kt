package com.example

import com.example.data.model.GeneticMarkerEntity
import com.example.data.model.PatientProfileEntity
import com.example.service.HipaaSecurityManager
import com.example.service.PrecisionMedicineEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testDiseaseRiskPrediction() {
    val brca1Marker = GeneticMarkerEntity(
      gene = "BRCA1",
      rsId = "rs80357906",
      chromosome = "chr17",
      genotype = "c.68_69delAG",
      phenotype = "Pathogenic Truncating Variant",
      category = "Oncology Risk",
      clinicalSignificance = "High Penetrance Neoplastic Predisposition",
      cpicOrAcmgLevel = "ACMG Tier 1",
      summary = "Impaired homologous recombination DNA repair",
      actionableRecommendations = "Biannual breast MRI"
    )

    val predictions = PrecisionMedicineEngine.calculateDiseaseRiskPredictions(listOf(brca1Marker))
    val breastCancerPrediction = predictions.find { it.conditionName.contains("Breast", ignoreCase = true) }
    assertTrue(breastCancerPrediction != null)
    assertEquals("HIGH RISK", breastCancerPrediction?.riskLevel)
    assertTrue(breastCancerPrediction?.lifetimeRiskPercent ?: 0 > 50)
  }

  @Test
  fun testSafeHarborDeIdentification() {
    val profile = PatientProfileEntity(
      id = 1,
      fullName = "Eleanor Vance",
      age = 42,
      gender = "Female",
      weightKg = 64.5f,
      heightCm = 168.0f,
      bloodType = "A+",
      systolic = 122,
      diastolic = 78,
      bloodGlucose = 98.4f,
      totalCholesterol = 192.0f,
      medicalHistory = "None",
      allergies = "None"
    )

    val export = HipaaSecurityManager.generateSafeHarborDeIdentifiedExport(profile, 6, 4)
    assertTrue(export.anonymizedSubjectId.startsWith("SUBJ-HIPAA-"))
    assertTrue(export.exportContentJson.contains("hipaa_safe_harbor_de_identified_record"))
    // Ensure full name is redacted
    assertTrue(!export.exportContentJson.contains("Eleanor Vance"))
  }

  @Test
  fun testSymptomScreenerCancerRedFlags() {
    val report = PrecisionMedicineEngine.screenSymptoms(
      selectedSymptoms = listOf("Unexplained weight loss (>10 lbs)", "Painless breast or axillary lump"),
      patientProfile = null,
      markers = emptyList()
    )
    assertEquals("Oncology & Cancer Warning Red Flags", report.suspectedCategory)
    assertEquals("URGENT_EVALUATION", report.triageLevel)
    assertTrue(report.recommendedFollowUpTests.contains("Biopsy"))
  }
}
