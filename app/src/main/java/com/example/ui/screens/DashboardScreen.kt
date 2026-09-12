package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Biotech
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActiveMedicationEntity
import com.example.data.model.EvaluatedInteraction
import com.example.data.model.GeneticMarkerEntity
import com.example.data.model.HealthTrendPointEntity
import com.example.data.model.PatientProfileEntity
import com.example.data.model.RoutineCheckupEntity
import com.example.ui.components.HipaaComplianceBanner
import com.example.ui.components.LongitudinalTrendChart
import com.example.ui.theme.DNAViolet
import com.example.ui.theme.MedicalCyan
import com.example.ui.theme.MedicalDarkBlue
import com.example.ui.theme.MedicalNavy
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.SeverityContraindicated
import com.example.ui.theme.SeveritySafe
import com.example.ui.theme.SeverityWarning
import java.util.Locale

@Composable
fun DashboardScreen(
  profile: PatientProfileEntity?,
  markers: List<GeneticMarkerEntity>,
  interactions: List<EvaluatedInteraction>,
  medications: List<ActiveMedicationEntity>,
  trendPoints: List<HealthTrendPointEntity>,
  checkups: List<RoutineCheckupEntity>,
  onNavigateToGenetics: () -> Unit,
  onNavigateToInteractions: () -> Unit,
  onNavigateToSymptoms: () -> Unit,
  onNavigateToTrends: () -> Unit,
  onNavigateToDiet: () -> Unit,
  onOpenSecurityCenter: () -> Unit,
  onUpdateDemographics: (Int, String, Float, Float, Int, Int, Float, Float, String, String) -> Unit,
  modifier: Modifier = Modifier
) {
  var showEditProfileDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("dashboard_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. HIPAA Compliance & Vault Status
    item {
      Spacer(modifier = Modifier.height(4.dp))
      HipaaComplianceBanner(onOpenSecurityCenter = onOpenSecurityCenter)
    }

    // 2. Patient Demographics & Vitals Executive Card
    item {
      PatientDemographicsCard(
        profile = profile,
        onEditClick = { showEditProfileDialog = true }
      )
    }

    // 3. High-Priority Pharmacogenomics & Genetic Alert Bar
    item {
      val criticalInteractions = interactions.filter {
        it.severity == EvaluatedInteraction.SeverityLevel.CONTRAINDICATED ||
          it.severity == EvaluatedInteraction.SeverityLevel.SEVERE
      }

      if (criticalInteractions.isNotEmpty()) {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = SeverityContraindicated.copy(alpha = 0.12f)),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToInteractions() }
            .testTag("critical_interaction_alert_card")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .background(SeverityContraindicated, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "${criticalInteractions.size} Pharmacogenomic Alerts Detected",
                fontWeight = FontWeight.Bold,
                color = SeverityContraindicated,
                fontSize = 14.sp
              )
              Text(
                text = "${criticalInteractions.first().drugName} clashes with ${criticalInteractions.first().interactingGeneOrDrug}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = SeverityContraindicated)
          }
        }
      }
    }

    // 4. Quick Action Modules (Genomics, Drug Checker, Symptom Screener, Precision Diet)
    item {
      Text(
        text = "Precision Health Modules",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        QuickModuleButton(
          title = "Genomics",
          subtitle = "${markers.size} Markers",
          icon = Icons.Outlined.Biotech,
          accentColor = DNAViolet,
          onClick = onNavigateToGenetics,
          modifier = Modifier.weight(1f).testTag("quick_genomics_button")
        )
        QuickModuleButton(
          title = "Drug Safety",
          subtitle = "${medications.size} Active",
          icon = Icons.Default.Medication,
          accentColor = MedicalTeal,
          onClick = onNavigateToInteractions,
          modifier = Modifier.weight(1f).testTag("quick_drug_safety_button")
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        QuickModuleButton(
          title = "Symptom AI",
          subtitle = "Cancer/Infection",
          icon = Icons.Default.Healing,
          accentColor = Color(0xFFE53E3E),
          onClick = onNavigateToSymptoms,
          modifier = Modifier.weight(1f).testTag("quick_symptoms_button")
        )
        QuickModuleButton(
          title = "Precision Diet",
          subtitle = "Genotype Diet",
          icon = Icons.Default.Psychology,
          accentColor = Color(0xFF2B6CB0),
          onClick = onNavigateToDiet,
          modifier = Modifier.weight(1f).testTag("quick_diet_button")
        )
      }
    }

    // 5. Longitudinal Health Trends Preview
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Health Trends Telemetry",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onBackground
        )
        TextButton(onClick = onNavigateToTrends) {
          Text("Full Analytics ›", color = MedicalTeal)
        }
      }
      LongitudinalTrendChart(trendPoints = trendPoints)
    }

    // 6. Routine Checkups Alert Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Genomic Checkup Surveillance",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = "${checkups.count { !it.isCompleted }} Pending",
          style = MaterialTheme.typography.labelMedium,
          color = MedicalTeal
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      checkups.take(3).forEach { checkup ->
        CheckupMiniCard(checkup = checkup)
        Spacer(modifier = Modifier.height(8.dp))
      }
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }

  // Edit Demographics Dialog
  if (showEditProfileDialog && profile != null) {
    EditDemographicsDialog(
      currentProfile = profile,
      onDismiss = { showEditProfileDialog = false },
      onSave = { age, gender, weight, height, sys, dia, glu, chol, hist, allerg ->
        onUpdateDemographics(age, gender, weight, height, sys, dia, glu, chol, hist, allerg)
        showEditProfileDialog = false
      }
    )
  }
}

@Composable
fun PatientDemographicsCard(
  profile: PatientProfileEntity?,
  onEditClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("patient_demographics_card")
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
              .size(46.dp)
              .background(
                brush = Brush.linearGradient(listOf(MedicalTeal, MedicalCyan)),
                shape = CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = profile?.fullName?.take(2)?.uppercase(Locale.US) ?: "EV",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = profile?.fullName ?: "Eleanor Vance",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${profile?.age ?: 42} Yrs • ${profile?.gender ?: "Female"} • Blood ${profile?.bloodType ?: "A+"}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        IconButton(
          onClick = onEditClick,
          modifier = Modifier.testTag("edit_profile_button")
        ) {
          Icon(Icons.Default.Edit, contentDescription = "Edit Vitals & Demographics", tint = MedicalTeal)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 4 Key Metrics Grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        MetricTile(
          label = "Weight",
          value = "${profile?.weightKg ?: 64.5f} kg",
          badge = "BMI ${profile?.formattedBmi ?: "22.9"}",
          badgeColor = SeveritySafe,
          modifier = Modifier.weight(1f)
        )
        MetricTile(
          label = "Height",
          value = "${profile?.heightCm?.toInt() ?: 168} cm",
          badge = profile?.bmiCategory ?: "Optimal",
          badgeColor = SeveritySafe,
          modifier = Modifier.weight(1f)
        )
        MetricTile(
          label = "Blood Pressure",
          value = "${profile?.systolic ?: 122}/${profile?.diastolic ?: 78}",
          badge = "Normal",
          badgeColor = SeveritySafe,
          modifier = Modifier.weight(1f)
        )
        MetricTile(
          label = "Fasting Glucose",
          value = "${profile?.bloodGlucose ?: 98.4f}",
          badge = "mg/dL",
          badgeColor = MedicalTeal,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Medical History & Allergies Badges
      if (!profile?.medicalHistory.isNullOrBlank()) {
        Text(
          text = "Integrated Medical History:",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = profile?.medicalHistory ?: "",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface,
          lineHeight = 16.sp
        )
      }

      if (!profile?.allergies.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Allergies: ",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = SeverityContraindicated
          )
          Text(
            text = profile?.allergies ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }
  }
}

@Composable
fun MetricTile(
  label: String,
  value: String,
  badge: String,
  badgeColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = label,
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(4.dp))
      Box(
        modifier = Modifier
          .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
          .padding(horizontal = 4.dp, vertical = 2.dp)
      ) {
        Text(
          text = badge,
          color = badgeColor,
          fontSize = 9.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
    }
  }
}

@Composable
fun QuickModuleButton(
  title: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  accentColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
    modifier = modifier.clickable { onClick() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .background(accentColor.copy(alpha = 0.15f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = title,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = subtitle,
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Composable
fun CheckupMiniCard(checkup: RoutineCheckupEntity) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (checkup.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
      else MaterialTheme.colorScheme.surface
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = checkup.title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.width(6.dp))
          if (checkup.urgency == "High") {
            Box(
              modifier = Modifier
                .background(SeverityContraindicated.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
              Text("High Risk", color = SeverityContraindicated, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
        Text(
          text = checkup.rationale,
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = checkup.dueDate,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = MedicalTeal
      )
    }
  }
}

@Composable
fun EditDemographicsDialog(
  currentProfile: PatientProfileEntity,
  onDismiss: () -> Unit,
  onSave: (Int, String, Float, Float, Int, Int, Float, Float, String, String) -> Unit
) {
  var ageText by remember { mutableStateOf(currentProfile.age.toString()) }
  var genderText by remember { mutableStateOf(currentProfile.gender) }
  var weightText by remember { mutableStateOf(currentProfile.weightKg.toString()) }
  var heightText by remember { mutableStateOf(currentProfile.heightCm.toString()) }
  var systolicText by remember { mutableStateOf(currentProfile.systolic.toString()) }
  var diastolicText by remember { mutableStateOf(currentProfile.diastolic.toString()) }
  var glucoseText by remember { mutableStateOf(currentProfile.bloodGlucose.toString()) }
  var cholesterolText by remember { mutableStateOf(currentProfile.totalCholesterol.toString()) }
  var historyText by remember { mutableStateOf(currentProfile.medicalHistory) }
  var allergiesText by remember { mutableStateOf(currentProfile.allergies) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        "Update Patient Vitals & Demographics",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = ageText,
            onValueChange = { ageText = it },
            label = { Text("Age") },
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = genderText,
            onValueChange = { genderText = it },
            label = { Text("Gender") },
            modifier = Modifier.weight(1f)
          )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = weightText,
            onValueChange = { weightText = it },
            label = { Text("Weight (kg)") },
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = heightText,
            onValueChange = { heightText = it },
            label = { Text("Height (cm)") },
            modifier = Modifier.weight(1f)
          )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = systolicText,
            onValueChange = { systolicText = it },
            label = { Text("Systolic BP") },
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = diastolicText,
            onValueChange = { diastolicText = it },
            label = { Text("Diastolic BP") },
            modifier = Modifier.weight(1f)
          )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = glucoseText,
            onValueChange = { glucoseText = it },
            label = { Text("Glucose (mg/dL)") },
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = cholesterolText,
            onValueChange = { cholesterolText = it },
            label = { Text("Cholesterol (mg/dL)") },
            modifier = Modifier.weight(1f)
          )
        }
        OutlinedTextField(
          value = historyText,
          onValueChange = { historyText = it },
          label = { Text("Medical History") },
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = allergiesText,
          onValueChange = { allergiesText = it },
          label = { Text("Allergies") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val age = ageText.toIntOrNull() ?: currentProfile.age
          val weight = weightText.toFloatOrNull() ?: currentProfile.weightKg
          val height = heightText.toFloatOrNull() ?: currentProfile.heightCm
          val sys = systolicText.toIntOrNull() ?: currentProfile.systolic
          val dia = diastolicText.toIntOrNull() ?: currentProfile.diastolic
          val glu = glucoseText.toFloatOrNull() ?: currentProfile.bloodGlucose
          val chol = cholesterolText.toFloatOrNull() ?: currentProfile.totalCholesterol
          onSave(age, genderText, weight, height, sys, dia, glu, chol, historyText, allergiesText)
        },
        colors = ButtonDefaults.buttonColors(containerColor = MedicalTeal)
      ) {
        Text("Save & Log Vitals")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
