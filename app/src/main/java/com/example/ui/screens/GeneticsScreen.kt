package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiseaseRiskPrediction
import com.example.data.model.GeneticMarkerEntity
import com.example.ui.theme.DNAViolet
import com.example.ui.theme.MedicalCyan
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.SeverityContraindicated
import com.example.ui.theme.SeveritySafe
import com.example.ui.theme.SeverityWarning

@Composable
fun GeneticsScreen(
  markers: List<GeneticMarkerEntity>,
  riskPredictions: List<DiseaseRiskPrediction>,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Risk Predictions, 1 = Genomic Catalog
  var selectedCategoryFilter by remember { mutableStateOf("All") }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("genetics_screen")
  ) {
    Spacer(modifier = Modifier.height(8.dp))

    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MedicalTeal
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("Future Disease Risks (${riskPredictions.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
        modifier = Modifier.testTag("tab_risk_predictions")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("Genomic Markers (${markers.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
        modifier = Modifier.testTag("tab_genomic_catalog")
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (selectedTab == 0) {
      // Future Disease & Disorder Risk Predictions
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        item {
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
              Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "Precision risk algorithms correlate whole-exome variants with multi-generational epidemiology to forecast penetrance and guide preventative action.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        items(riskPredictions) { prediction ->
          DiseaseRiskPredictionCard(prediction = prediction)
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
      }
    } else {
      // Genomic Catalog with Category Filter
      val categories = listOf("All", "Pharmacogenomics", "Oncology Risk", "Cardiovascular", "Metabolic & Chronic")
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        categories.take(3).forEach { cat ->
          FilterChip(
            selected = selectedCategoryFilter == cat,
            onClick = { selectedCategoryFilter = cat },
            label = { Text(cat, fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = DNAViolet,
              selectedLabelColor = Color.White
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      val filteredMarkers = if (selectedCategoryFilter == "All") markers
      else markers.filter { it.category.contains(selectedCategoryFilter, ignoreCase = true) }

      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredMarkers) { marker ->
          GeneticMarkerCard(marker = marker)
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
      }
    }
  }
}

@Composable
fun DiseaseRiskPredictionCard(prediction: DiseaseRiskPrediction) {
  var isExpanded by remember { mutableStateOf(false) }

  val riskColor = when (prediction.riskLevel) {
    "HIGH RISK" -> SeverityContraindicated
    "ELEVATED" -> SeverityWarning
    "MODERATE" -> Color(0xFF2563EB)
    else -> SeveritySafe
  }

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { isExpanded = !isExpanded }
      .testTag("risk_card_${prediction.conditionName.take(10)}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = prediction.category.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = riskColor
          )
          Text(
            text = prediction.conditionName,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Box(
          modifier = Modifier
            .background(riskColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = prediction.riskLevel,
            color = riskColor,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Lifetime Risk vs Population Average Progress Visualizer
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Patient Lifetime Risk: ${prediction.lifetimeRiskPercent}%",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Pop. Avg: ${prediction.averagePopulationRiskPercent}% (${prediction.relativeRiskMultiplier}x)",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      LinearProgressIndicator(
        progress = { (prediction.lifetimeRiskPercent / 100f).coerceIn(0f, 1f) },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp),
        color = riskColor,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Biotech, contentDescription = null, tint = DNAViolet, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Key Driver: ${prediction.keyMarkers}",
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // Expandable Actionable Surveillance & Lifestyle details
      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(top = 12.dp)) {
          Text(
            text = "Tailored Preventative Lifestyle Modifications:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(6.dp))
          prediction.preventativeLifestyle.forEach { act ->
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
              Text("• ", color = MedicalTeal, fontWeight = FontWeight.Bold)
              Text(act, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Recommended Clinical Surveillance & Diagnostics:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(6.dp))
          prediction.recommendedSurveillance.forEach { surv ->
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
              Text("✓ ", color = SeveritySafe, fontWeight = FontWeight.Bold)
              Text(surv, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Composable
fun GeneticMarkerCard(marker: GeneticMarkerEntity) {
  val isHighRisk = marker.clinicalSignificance.contains("Pathogenic", ignoreCase = true) ||
    marker.clinicalSignificance.contains("Critical", ignoreCase = true) ||
    marker.clinicalSignificance.contains("High", ignoreCase = true)

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .background(if (isHighRisk) SeverityContraindicated.copy(alpha = 0.15f) else DNAViolet.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Biotech,
              contentDescription = null,
              tint = if (isHighRisk) SeverityContraindicated else DNAViolet,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = marker.gene,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Genotype: ${marker.genotype} • ${marker.rsId} (${marker.chromosome})",
              style = MaterialTheme.typography.bodySmall,
              fontFamily = FontFamily.Monospace,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Box(
          modifier = Modifier
            .background(
              if (isHighRisk) SeverityContraindicated.copy(alpha = 0.15f) else SeveritySafe.copy(alpha = 0.15f),
              RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
          Text(
            text = marker.cpicOrAcmgLevel,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (isHighRisk) SeverityContraindicated else SeveritySafe
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "Phenotype: ${marker.phenotype}",
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        color = if (isHighRisk) SeverityContraindicated else MedicalTeal
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = marker.summary,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(8.dp))

      Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Text(
            text = "Precision Clinical Action:",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = marker.actionableRecommendations,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}
