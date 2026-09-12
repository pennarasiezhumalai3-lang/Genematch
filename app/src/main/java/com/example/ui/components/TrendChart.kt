package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HealthTrendPointEntity
import com.example.ui.theme.MedicalCyan
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.SeverityContraindicated
import com.example.ui.theme.SeveritySafe
import com.example.ui.theme.SeverityWarning
import java.util.Locale

enum class TrendMetric(val label: String, val unit: String) {
  BLOOD_PRESSURE("Blood Pressure", "mmHg"),
  GLUCOSE("Fasting Glucose", "mg/dL"),
  WEIGHT("Weight & BMI", "kg"),
  CHOLESTEROL("LDL Cholesterol", "mg/dL")
}

@Composable
fun LongitudinalTrendChart(
  trendPoints: List<HealthTrendPointEntity>,
  modifier: Modifier = Modifier
) {
  var selectedMetric by remember { mutableStateOf(TrendMetric.BLOOD_PRESSURE) }
  var selectedIndex by remember { mutableIntStateOf(-1) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("longitudinal_trend_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Longitudinal Health Biomarkers",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "6-Month Trend & Genetic Target Compliance",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Metric Filter Chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TrendMetric.values().forEach { metric ->
          FilterChip(
            selected = selectedMetric == metric,
            onClick = {
              selectedMetric = metric
              selectedIndex = -1
            },
            label = { Text(metric.label, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MedicalTeal,
              selectedLabelColor = Color.White
            ),
            modifier = Modifier.testTag("filter_chip_${metric.name.lowercase(Locale.US)}")
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (trendPoints.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
          contentAlignment = Alignment.Center
        ) {
          Text("No longitudinal telemetry recorded yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      } else {
        // Render Canvas Line Chart
        val points = trendPoints
        val dates = points.map { it.dateLabel }

        // Primary and secondary series depending on metric
        val primaryValues = when (selectedMetric) {
          TrendMetric.BLOOD_PRESSURE -> points.map { it.systolic.toFloat() }
          TrendMetric.GLUCOSE -> points.map { it.bloodGlucose }
          TrendMetric.WEIGHT -> points.map { it.weightKg }
          TrendMetric.CHOLESTEROL -> points.map { it.cholesterolLdl }
        }

        val secondaryValues = if (selectedMetric == TrendMetric.BLOOD_PRESSURE) {
          points.map { it.diastolic.toFloat() }
        } else null

        val allValues = if (secondaryValues != null) primaryValues + secondaryValues else primaryValues
        val minVal = (allValues.minOrNull() ?: 0f) * 0.9f
        val maxVal = (allValues.maxOrNull() ?: 100f) * 1.1f
        val range = if (maxVal > minVal) maxVal - minVal else 1f

        val primaryColor = when (selectedMetric) {
          TrendMetric.BLOOD_PRESSURE -> SeverityContraindicated
          TrendMetric.GLUCOSE -> SeverityWarning
          TrendMetric.WEIGHT -> MedicalTeal
          TrendMetric.CHOLESTEROL -> Color(0xFF8B5CF6)
        }
        val secondaryColor = MedicalCyan

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
          Canvas(
            modifier = Modifier
              .matchParentSize()
              .pointerInput(points.size) {
                detectTapGestures { tapOffset ->
                  val step = size.width / (points.size - 1).coerceAtLeast(1)
                  val idx = ((tapOffset.x + step / 2) / step).toInt().coerceIn(0, points.size - 1)
                  selectedIndex = idx
                }
              }
          ) {
            val width = size.width
            val height = size.height
            val stepX = width / (points.size - 1).coerceAtLeast(1)

            // Draw horizontal guideline bands (Normal vs Elevated)
            val gridLines = 4
            for (i in 0..gridLines) {
              val y = height - (i * height / gridLines)
              drawLine(
                color = Color.LightGray.copy(alpha = 0.35f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
              )
            }

            // Draw Primary Path
            val primaryPath = Path()
            val primaryOffsets = mutableListOf<Offset>()

            points.forEachIndexed { i, _ ->
              val x = i * stepX
              val y = height - ((primaryValues[i] - minVal) / range * height).coerceIn(0f, height)
              val offset = Offset(x, y)
              primaryOffsets.add(offset)
              if (i == 0) primaryPath.moveTo(x, y) else primaryPath.lineTo(x, y)
            }

            drawPath(
              path = primaryPath,
              color = primaryColor,
              style = Stroke(width = 3.dp.toPx())
            )

            // Draw Secondary Path if Blood Pressure (Diastolic)
            if (secondaryValues != null) {
              val secPath = Path()
              val secOffsets = mutableListOf<Offset>()
              points.forEachIndexed { i, _ ->
                val x = i * stepX
                val y = height - ((secondaryValues[i] - minVal) / range * height).coerceIn(0f, height)
                secOffsets.add(Offset(x, y))
                if (i == 0) secPath.moveTo(x, y) else secPath.lineTo(x, y)
              }
              drawPath(
                path = secPath,
                color = secondaryColor,
                style = Stroke(width = 2.5.dp.toPx())
              )
              // Draw secondary circles
              secOffsets.forEachIndexed { i, off ->
                drawCircle(
                  color = secondaryColor,
                  radius = if (i == selectedIndex) 7.dp.toPx() else 4.dp.toPx(),
                  center = off
                )
              }
            }

            // Draw Primary circles
            primaryOffsets.forEachIndexed { i, off ->
              drawCircle(
                color = primaryColor,
                radius = if (i == selectedIndex) 8.dp.toPx() else 4.5.dp.toPx(),
                center = off
              )
            }
          }
        }

        // Timeline Labels & Active Inspection Tooltip
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          dates.forEachIndexed { idx, label ->
            Text(
              text = label,
              fontSize = 10.sp,
              fontWeight = if (idx == selectedIndex) FontWeight.Bold else FontWeight.Normal,
              color = if (idx == selectedIndex) MedicalTeal else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Selected point readout card
        val activeIndex = if (selectedIndex in points.indices) selectedIndex else points.lastIndex
        val currentPoint = points[activeIndex]

        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Log Date: ${currentPoint.dateLabel}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = when (selectedMetric) {
                  TrendMetric.BLOOD_PRESSURE -> "${currentPoint.systolic} / ${currentPoint.diastolic} mmHg"
                  TrendMetric.GLUCOSE -> "${String.format(Locale.US, "%.1f", currentPoint.bloodGlucose)} mg/dL"
                  TrendMetric.WEIGHT -> "${String.format(Locale.US, "%.1f", currentPoint.weightKg)} kg"
                  TrendMetric.CHOLESTEROL -> "${String.format(Locale.US, "%.1f", currentPoint.cholesterolLdl)} mg/dL"
                },
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            // Clinical interpretation status pill
            val (statusText, statusColor) = when (selectedMetric) {
              TrendMetric.BLOOD_PRESSURE -> {
                if (currentPoint.systolic <= 120 && currentPoint.diastolic <= 80) "Optimal Range" to SeveritySafe
                else if (currentPoint.systolic <= 130) "Prehypertension" to SeverityWarning
                else "Stage 1 HTN" to SeverityContraindicated
              }
              TrendMetric.GLUCOSE -> {
                if (currentPoint.bloodGlucose < 100f) "Normal Fasting" to SeveritySafe
                else "Impaired (TCF7L2)" to SeverityWarning
              }
              TrendMetric.WEIGHT -> "BMI 22.9 (Healthy)" to SeveritySafe
              TrendMetric.CHOLESTEROL -> {
                if (currentPoint.cholesterolLdl < 120f) "Target Met (<130)" to SeveritySafe
                else "Elevated (APOE)" to SeverityWarning
              }
            }

            Box(
              modifier = Modifier
                .background(statusColor.copy(alpha = 0.15f), shape = RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = statusText,
                color = statusColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }
  }
}
