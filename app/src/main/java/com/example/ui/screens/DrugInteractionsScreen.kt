package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.ActiveMedicationEntity
import com.example.data.model.EvaluatedInteraction
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.SeverityContraindicated
import com.example.ui.theme.SeverityModerate
import com.example.ui.theme.SeveritySafe
import com.example.ui.theme.SeverityWarning

@Composable
fun DrugInteractionsScreen(
  medications: List<ActiveMedicationEntity>,
  interactions: List<EvaluatedInteraction>,
  onAddMedication: (String, String, String, String) -> Unit,
  onRemoveMedication: (Long, String) -> Unit,
  modifier: Modifier = Modifier
) {
  var showAddDialog by remember { mutableStateOf(false) }

  Scaffold(
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddDialog = true },
        containerColor = MedicalTeal,
        contentColor = Color.White,
        modifier = Modifier.testTag("add_medication_fab")
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Medication")
      }
    },
    containerColor = Color.Transparent,
    modifier = modifier.testTag("drug_interactions_screen")
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 16.dp),
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
            Icon(Icons.Default.Medication, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Real-time pharmacogenomic & drug interaction safety engine actively screens medications against patient's CYP450, SLCO1B1, and VKORC1 genotypes.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // 1. Evaluated Interaction Alerts
      item {
        Text(
          text = "Pharmacogenomic & Interaction Alerts (${interactions.size})",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onBackground
        )
      }

      if (interactions.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
              Text("No interactions detected with current regimen.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        }
      } else {
        items(interactions) { interaction ->
          InteractionAlertCard(interaction = interaction)
        }
      }

      // 2. Active Medications List
      item {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Active Medications (${medications.size})",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )
        }
      }

      items(medications) { med ->
        ActiveMedicationCard(
          med = med,
          onDelete = { onRemoveMedication(med.id, med.drugName) }
        )
      }

      item { Spacer(modifier = Modifier.height(72.dp)) }
    }
  }

  if (showAddDialog) {
    AddMedicationDialog(
      onDismiss = { showAddDialog = false },
      onConfirm = { name, dose, freq, reason ->
        onAddMedication(name, dose, freq, reason)
        showAddDialog = false
      }
    )
  }
}

@Composable
fun InteractionAlertCard(interaction: EvaluatedInteraction) {
  val (bgColor, iconTint, icon) = when (interaction.severity) {
    EvaluatedInteraction.SeverityLevel.CONTRAINDICATED -> Triple(SeverityContraindicated.copy(alpha = 0.1f), SeverityContraindicated, Icons.Default.Error)
    EvaluatedInteraction.SeverityLevel.SEVERE -> Triple(SeverityWarning.copy(alpha = 0.12f), SeverityWarning, Icons.Default.Warning)
    EvaluatedInteraction.SeverityLevel.MODERATE -> Triple(SeverityModerate.copy(alpha = 0.1f), SeverityModerate, Icons.Default.Info)
    EvaluatedInteraction.SeverityLevel.SAFE -> Triple(SeveritySafe.copy(alpha = 0.1f), SeveritySafe, Icons.Default.CheckCircle)
  }

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("interaction_card_${interaction.drugName.take(8)}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .background(bgColor, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = interaction.drugName,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Conflict: ${interaction.interactingGeneOrDrug}",
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.Medium,
              color = iconTint
            )
          }
        }

        Box(
          modifier = Modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = interaction.severity.name,
            color = iconTint,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "Biological Mechanism:",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = interaction.biologicalMechanism,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = 17.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Clinical Guidance: ${interaction.clinicalGuidance}",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = iconTint
      )

      if (interaction.recommendedAlternatives.isNotEmpty() && interaction.severity != EvaluatedInteraction.SeverityLevel.SAFE) {
        Spacer(modifier = Modifier.height(10.dp))
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(
              text = "Personalized Precision Alternative Drugs:",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            interaction.recommendedAlternatives.forEach { alt ->
              Text("→ $alt", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MedicalTeal)
            }
          }
        }
      }
    }
  }
}

@Composable
fun ActiveMedicationCard(
  med: ActiveMedicationEntity,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .background(MedicalTeal.copy(alpha = 0.12f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Medication, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = med.drugName,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${med.dosage} • ${med.frequency}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          if (med.reason.isNotBlank()) {
            Text(
              text = "Indication: ${med.reason}",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      IconButton(onClick = onDelete) {
        Icon(Icons.Default.Delete, contentDescription = "Delete Medication", tint = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  }
}

@Composable
fun AddMedicationDialog(
  onDismiss: () -> Unit,
  onConfirm: (String, String, String, String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var dosage by remember { mutableStateOf("") }
  var frequency by remember { mutableStateOf("Once Daily") }
  var reason by remember { mutableStateOf("") }

  val quickMedList = listOf(
    "Codeine" to "30 mg",
    "Warfarin" to "5 mg",
    "Omeprazole" to "20 mg",
    "Aspirin" to "81 mg",
    "Tramadol" to "50 mg",
    "Abacavir" to "300 mg"
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Add Medication to Regimen", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "Quick Select for Pharmacogenomic Testing:",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          quickMedList.take(3).forEach { (mName, mDose) ->
            SuggestionChip(
              onClick = {
                name = mName
                dosage = mDose
              },
              label = { Text(mName, fontSize = 10.sp) }
            )
          }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          quickMedList.drop(3).forEach { (mName, mDose) ->
            SuggestionChip(
              onClick = {
                name = mName
                dosage = mDose
              },
              label = { Text(mName, fontSize = 10.sp) }
            )
          }
        }

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Drug Name") },
          modifier = Modifier.fillMaxWidth().testTag("add_med_name_input")
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = dosage,
            onValueChange = { dosage = it },
            label = { Text("Dosage (e.g. 50mg)") },
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = frequency,
            onValueChange = { frequency = it },
            label = { Text("Frequency") },
            modifier = Modifier.weight(1f)
          )
        }
        OutlinedTextField(
          value = reason,
          onValueChange = { reason = it },
          label = { Text("Indication / Reason") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            onConfirm(name, dosage.ifEmpty { "Standard dose" }, frequency, reason)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = MedicalTeal),
        modifier = Modifier.testTag("confirm_add_med_button")
      ) {
        Text("Evaluate Safety")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}
