package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SymptomReportEntity
import com.example.ui.theme.DNAViolet
import com.example.ui.theme.MedicalCyan
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.SeverityContraindicated
import com.example.ui.theme.SeveritySafe
import com.example.ui.theme.SeverityWarning
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SymptomDiagnosticScreen(
  symptomReports: List<SymptomReportEntity>,
  onRunAssessment: (List<String>, (SymptomReportEntity) -> Unit) -> Unit,
  onSendToProvider: (String, String, String) -> Unit,
  modifier: Modifier = Modifier
) {
  val selectedSymptoms = remember { mutableStateListOf<String>() }
  var latestReport by remember { mutableStateOf<SymptomReportEntity?>(null) }
  var isAnalyzing by remember { mutableStateOf(false) }

  // 4 Core requested symptom categories
  val cancerSymptoms = listOf(
    "Unexplained weight loss (>10 lbs)",
    "Painless breast or axillary lump",
    "Persistent cough with blood / hemoptysis",
    "Changing asymmetric mole (ABCDE criteria)",
    "Difficulty swallowing / persistent dysphagia",
    "Persistent bowel habit changes / hematochezia",
    "Drenching night sweats & lymphadenopathy"
  )

  val viralSymptoms = listOf(
    "Sudden high fever (>102°F / 38.9°C)",
    "Severe retro-orbital eye headache",
    "Severe generalized muscle & body aches",
    "Diffuse maculopapular rash",
    "Profound post-viral fatigue & chills"
  )

  val bacterialSymptoms = listOf(
    "Productive cough with purulent / rust sputum",
    "Severe unilateral flank pain & dysuria",
    "Localized hot, erythematous skin swelling",
    "Tonsillar exudates & anterior cervical adenopathy",
    "Rigors / severe shivering with hypotension"
  )

  val chronicOnsetSymptoms = listOf(
    "Excessive thirst (polydipsia) & polyuria",
    "Morning joint stiffness lasting > 1 hour",
    "Recurrent blurred vision with occipital headache",
    "Bilateral pedal edema (ankle swelling)",
    "Peripheral tingling / numbness in extremities"
  )

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("symptom_diagnostic_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Healing, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Select any presenting symptoms below to generate an automated preliminary diagnostic report with tailored follow-up laboratory testing and genetic correlation.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // Latest Generated Report Modal Card if available
    if (latestReport != null) {
      item {
        GeneratedDiagnosticReportCard(
          report = latestReport!!,
          onDismiss = { latestReport = null },
          onSendToDoctor = {
            onSendToProvider(
              "Dr. Chen, I ran a symptom screening report (${latestReport!!.suspectedCategory}). The preliminary findings indicate ${latestReport!!.triageLevel} triage. Please review the attached diagnostic report.",
              "Diagnostic Report: ${latestReport!!.suspectedCategory}",
              "SYMPTOM_REPORT"
            )
          }
        )
      }
    }

    // Category 1: Oncology & Cancer Warning Red Flags
    item {
      SymptomCategorySection(
        categoryTitle = "1. Cancer & Oncology Warning Signs",
        categorySubtitle = "Early detection of solid tumors & neoplastic onset",
        badgeColor = SeverityContraindicated,
        symptoms = cancerSymptoms,
        selectedSymptoms = selectedSymptoms,
        onToggle = { symptom ->
          if (selectedSymptoms.contains(symptom)) selectedSymptoms.remove(symptom)
          else selectedSymptoms.add(symptom)
        }
      )
    }

    // Category 2: Viral Fever Syndromes
    item {
      SymptomCategorySection(
        categoryTitle = "2. Viral Fever Syndromes",
        categorySubtitle = "Influenza, Dengue, COVID-19, Arboviral presentations",
        badgeColor = Color(0xFF805AD5),
        symptoms = viralSymptoms,
        selectedSymptoms = selectedSymptoms,
        onToggle = { symptom ->
          if (selectedSymptoms.contains(symptom)) selectedSymptoms.remove(symptom)
          else selectedSymptoms.add(symptom)
        }
      )
    }

    // Category 3: Bacterial Infections
    item {
      SymptomCategorySection(
        categoryTitle = "3. Bacterial Infections",
        categorySubtitle = "Focal purulence, pneumonia, pyelonephritis, sepsis criteria",
        badgeColor = Color(0xFFDD6B20),
        symptoms = bacterialSymptoms,
        selectedSymptoms = selectedSymptoms,
        onToggle = { symptom ->
          if (selectedSymptoms.contains(symptom)) selectedSymptoms.remove(symptom)
          else selectedSymptoms.add(symptom)
        }
      )
    }

    // Category 4: Chronic Conditions Onset
    item {
      SymptomCategorySection(
        categoryTitle = "4. Chronic Conditions Onset",
        categorySubtitle = "Type 2 Diabetes, Hypertension, Autoimmune Rheumatoid",
        badgeColor = MedicalCyan,
        symptoms = chronicOnsetSymptoms,
        selectedSymptoms = selectedSymptoms,
        onToggle = { symptom ->
          if (selectedSymptoms.contains(symptom)) selectedSymptoms.remove(symptom)
          else selectedSymptoms.add(symptom)
        }
      )
    }

    // Assessment Execution Action Button
    item {
      Button(
        onClick = {
          if (selectedSymptoms.isNotEmpty()) {
            isAnalyzing = true
            onRunAssessment(selectedSymptoms.toList()) { report ->
              isAnalyzing = false
              latestReport = report
            }
          }
        },
        enabled = selectedSymptoms.isNotEmpty() && !isAnalyzing,
        colors = ButtonDefaults.buttonColors(containerColor = MedicalTeal),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("run_diagnostic_button")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isAnalyzing) "Analyzing Clinical Markers..."
            else "Analyze ${selectedSymptoms.size} Symptoms & Generate Diagnostic Report",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
      }
    }

    // Historical Reports
    if (symptomReports.isNotEmpty()) {
      item {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Historical Diagnostic Reports (${symptomReports.size})",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onBackground
        )
      }

      items(symptomReports) { rep ->
        HistoricalReportItem(
          report = rep,
          onSelect = { latestReport = rep }
        )
      }
    }

    item { Spacer(modifier = Modifier.height(24.dp)) }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SymptomCategorySection(
  categoryTitle: String,
  categorySubtitle: String,
  badgeColor: Color,
  symptoms: List<String>,
  selectedSymptoms: List<String>,
  onToggle: (String) -> Unit
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(10.dp)
            .background(badgeColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = categoryTitle,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = categorySubtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        symptoms.forEach { symptom ->
          val isSelected = selectedSymptoms.contains(symptom)
          FilterChip(
            selected = isSelected,
            onClick = { onToggle(symptom) },
            label = {
              Text(
                text = symptom,
                fontSize = 11.sp,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = badgeColor,
              selectedLabelColor = Color.White
            ),
            leadingIcon = if (isSelected) {
              { Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp)) }
            } else null,
            modifier = Modifier.testTag("chip_${symptom.take(12).replace(" ", "_")}")
          )
        }
      }
    }
  }
}

@Composable
fun GeneratedDiagnosticReportCard(
  report: SymptomReportEntity,
  onDismiss: () -> Unit,
  onSendToDoctor: () -> Unit
) {
  val triageColor = when (report.triageLevel) {
    "EMERGENCY" -> SeverityContraindicated
    "URGENT_EVALUATION" -> SeverityWarning
    else -> SeveritySafe
  }

  var sentToDoctor by remember { mutableStateOf(false) }

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("diagnostic_report_card")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .background(triageColor.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.LocalHospital, contentDescription = null, tint = triageColor, modifier = Modifier.size(20.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Preliminary Diagnostic Report",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = report.suspectedCategory,
              style = MaterialTheme.typography.bodySmall,
              color = triageColor,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        Box(
          modifier = Modifier
            .background(triageColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = report.triageLevel.replace("_", " "),
            color = triageColor,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "Primary Clinical Hypothesis:",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = report.preliminaryDiagnosis,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = report.clinicalFindings,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 17.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Automated Recommendations for Follow-Up Testing
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Assignment, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Automated Follow-Up Diagnostic Testing Recommendations:",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          report.recommendedFollowUpTests.split(", ").forEach { testItem ->
            Text(
              text = "• $testItem",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurface,
              lineHeight = 16.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Genomic Correlation Note
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = DNAViolet.copy(alpha = 0.08f))
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.Top
        ) {
          Icon(Icons.Default.Biotech, contentDescription = null, tint = DNAViolet, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = report.genomicCorrelationNote,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 16.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        OutlinedButton(onClick = onDismiss) {
          Text("Dismiss")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
          onClick = {
            onSendToDoctor()
            sentToDoctor = true
          },
          colors = ButtonDefaults.buttonColors(containerColor = MedicalTeal),
          modifier = Modifier.testTag("send_report_to_doctor_button")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (sentToDoctor) "Transmitted to Dr. Chen ✓" else "Transmit to Doctor")
          }
        }
      }
    }
  }
}

@Composable
fun HistoricalReportItem(
  report: SymptomReportEntity,
  onSelect: () -> Unit
) {
  val sdf = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.US)
  val dateStr = sdf.format(Date(report.timestamp))

  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onSelect() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = report.suspectedCategory,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Triage: ${report.triageLevel} • $dateStr",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = report.symptomsReported,
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1
        )
      }

      Text(
        text = "View ›",
        color = MedicalTeal,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}
