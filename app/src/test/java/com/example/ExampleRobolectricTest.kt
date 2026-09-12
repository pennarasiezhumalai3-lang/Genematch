package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ActiveMedicationEntity
import com.example.data.model.EvaluatedInteraction
import com.example.data.model.GeneticMarkerEntity
import com.example.service.PrecisionMedicineEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("PrecisionRx", appName)
  }

  @Test
  fun `test pharmacogenomics drug interaction detection`() {
    val meds = listOf(
      ActiveMedicationEntity(drugName = "Clopidogrel", dosage = "75mg", frequency = "Daily", reason = "Antiplatelet")
    )
    val markers = listOf(
      GeneticMarkerEntity(
        gene = "CYP2C19",
        rsId = "rs4244285",
        chromosome = "chr10",
        genotype = "*2/*2",
        phenotype = "Poor Metabolizer",
        category = "Pharmacogenomics",
        clinicalSignificance = "CPIC Level 1A - Severe Loss of Function",
        cpicOrAcmgLevel = "CPIC Level 1A",
        summary = "No bioactivation",
        actionableRecommendations = "Switch to Ticagrelor"
      )
    )

    val interactions = PrecisionMedicineEngine.evaluateDrugInteractions(meds, markers)
    assertEquals(1, interactions.size)
    assertEquals(EvaluatedInteraction.SeverityLevel.CONTRAINDICATED, interactions.first().severity)
    assertTrue(interactions.first().recommendedAlternatives.isNotEmpty())
  }
}
