package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HealthTrendPointEntity
import com.example.ui.components.LongitudinalTrendChart
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.SeveritySafe
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VitalsLongitudinalScreen(
  trendPoints: List<HealthTrendPointEntity>,
  onAddVitalLog: (Int, Int, Float, Float, Float) -> Unit,
  modifier: Modifier = Modifier
) {
  var showAddDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("vitals_longitudinal_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Longitudinal Biomarker Trends",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "Continuous clinical tracking calibrated to genetic targets",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Button(
          onClick = { showAddDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = MedicalTeal),
          modifier = Modifier.testTag("add_vital_entry_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Log Vitals", fontSize = 12.sp)
        }
      }
    }

    // Interactive Multi-Metric Canvas Chart
    item {
      LongitudinalTrendChart(trendPoints = trendPoints)
    }

    // Statistical Summary Cards
    item {
      Text(
        text = "6-Month Statistical Averages",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(8.dp))

      val avgSys = if (trendPoints.isNotEmpty()) trendPoints.map { it.systolic }.average().toInt() else 122
      val avgDia = if (trendPoints.isNotEmpty()) trendPoints.map { it.diastolic }.average().toInt() else 78
      val avgGlu = if (trendPoints.isNotEmpty()) trendPoints.map { it.bloodGlucose }.average() else 98.4
      val avgWeight = if (trendPoints.isNotEmpty()) trendPoints.map { it.weightKg }.average() else 64.5

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Mean BP", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("$avgSys/$avgDia", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("mmHg", fontSize = 9.sp, color = SeveritySafe)
          }
        }
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Mean Glucose", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(String.format(Locale.US, "%.1f", avgGlu), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("mg/dL", fontSize = 9.sp, color = SeveritySafe)
          }
        }
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Mean Weight", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(String.format(Locale.US, "%.1f", avgWeight), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("kg (BMI 22.9)", fontSize = 9.sp, color = SeveritySafe)
          }
        }
      }
    }

    // Historical Entries List
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Recorded Telemetry Logs (${trendPoints.size})",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )
    }

    items(trendPoints.reversed()) { pt ->
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(text = pt.dateLabel, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(
              text = "Blood Pressure: ${pt.systolic}/${pt.diastolic} mmHg • Fasting Glucose: ${pt.bloodGlucose} mg/dL",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Column(horizontalAlignment = Alignment.End) {
            Text(text = "${pt.weightKg} kg", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Text(text = "LDL ${pt.cholesterolLdl} mg/dL", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      }
    }

    item { Spacer(modifier = Modifier.height(24.dp)) }
  }

  if (showAddDialog) {
    var sys by remember { mutableStateOf("120") }
    var dia by remember { mutableStateOf("80") }
    var glu by remember { mutableStateOf("98.0") }
    var wt by remember { mutableStateOf("64.5") }
    var chol by remember { mutableStateOf("115.0") }

    AlertDialog(
      onDismissRequest = { showAddDialog = false },
      title = { Text("Log New Biomarker Reading", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = sys, onValueChange = { sys = it }, label = { Text("Systolic") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = dia, onValueChange = { dia = it }, label = { Text("Diastolic") }, modifier = Modifier.weight(1f))
          }
          OutlinedTextField(value = glu, onValueChange = { glu = it }, label = { Text("Fasting Glucose (mg/dL)") }, modifier = Modifier.fillMaxWidth())
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = wt, onValueChange = { wt = it }, label = { Text("Weight (kg)") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = chol, onValueChange = { chol = it }, label = { Text("LDL Chol (mg/dL)") }, modifier = Modifier.weight(1f))
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val s = sys.toIntOrNull() ?: 120
            val d = dia.toIntOrNull() ?: 80
            val g = glu.toFloatOrNull() ?: 98.0f
            val w = wt.toFloatOrNull() ?: 64.5f
            val c = chol.toFloatOrNull() ?: 115.0f
            onAddVitalLog(s, d, g, w, c)
            showAddDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = MedicalTeal)
        ) {
          Text("Save & Add to Trend")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
      }
    )
  }
}
