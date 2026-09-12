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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.RoutineCheckupEntity
import com.example.service.PrecisionDietProtocol
import com.example.ui.theme.DNAViolet
import com.example.ui.theme.MedicalCyan
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.SeverityContraindicated
import com.example.ui.theme.SeveritySafe
import com.example.ui.theme.SeverityWarning

@Composable
fun DietLifestyleScreen(
  dietProtocols: List<PrecisionDietProtocol>,
  checkups: List<RoutineCheckupEntity>,
  onToggleCheckup: (Long, Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("diet_lifestyle_screen"),
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
          Icon(Icons.Default.Restaurant, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Nutrigenomics and epigenetic protocols custom-tailored to your lipid metabolism, homocysteine methylation, and insulin receptor genotypes.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // Genotype Diet Protocols
    item {
      Text(
        text = "Nutrigenomic Protocols (${dietProtocols.size})",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )
    }

    items(dietProtocols) { proto ->
      DietProtocolCard(protocol = proto)
    }

    // Routine Checkups & Surveillance Reminders
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Personalized Clinical Checkup Schedule (${checkups.size})",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )
    }

    items(checkups) { checkup ->
      CheckupInteractiveItem(
        checkup = checkup,
        onToggle = { isChecked -> onToggleCheckup(checkup.id, isChecked) }
      )
    }

    item { Spacer(modifier = Modifier.height(24.dp)) }
  }
}

@Composable
fun DietProtocolCard(protocol: PrecisionDietProtocol) {
  var isExpanded by remember { mutableStateOf(false) }

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { isExpanded = !isExpanded }
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
              .background(MedicalTeal.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Restaurant, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(20.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = protocol.title,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Target Genotype: ${protocol.targetGene}",
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              color = DNAViolet
            )
          }
        }

        Icon(
          imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = protocol.rationale,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = 17.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Macronutrient Bar
      Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
          horizontalArrangement = Arrangement.Center
        ) {
          Text(
            text = "Macro Target: ${protocol.macronutrientDistribution}",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MedicalTeal
          )
        }
      }

      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(top = 12.dp)) {
          // Foods to Emphasize
          Text(
            text = "Recommended Superfoods:",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = SeveritySafe
          )
          Spacer(modifier = Modifier.height(4.dp))
          protocol.recommendedFoods.forEach { food ->
            Text("✓ $food", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Foods to Avoid
          Text(
            text = "Foods to Restrict / Eliminate:",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = SeverityContraindicated
          )
          Spacer(modifier = Modifier.height(4.dp))
          protocol.avoidFoods.forEach { badFood ->
            Text("✗ $badFood", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Micronutrients & Supplements
          Text(
            text = "Precision Micronutrients & Co-factors:",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MedicalTeal
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = protocol.micronutrientSupplementation,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 16.sp
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Lifestyle Habits
          Text(
            text = "Hydration & Epigenetic Lifestyle:",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = protocol.hydrationAndLifestyle,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 16.sp
          )
        }
      }
    }
  }
}

@Composable
fun CheckupInteractiveItem(
  checkup: RoutineCheckupEntity,
  onToggle: (Boolean) -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (checkup.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      else MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Checkbox(
        checked = checkup.isCompleted,
        onCheckedChange = onToggle,
        colors = CheckboxDefaults.colors(checkedColor = MedicalTeal),
        modifier = Modifier.testTag("checkup_cb_${checkup.id}")
      )

      Spacer(modifier = Modifier.width(8.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = checkup.title,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = if (checkup.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.width(6.dp))
          if (checkup.urgency == "High") {
            Box(
              modifier = Modifier
                .background(SeverityContraindicated.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
              Text("High Urgency", color = SeverityContraindicated, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
        Text(
          text = checkup.rationale,
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = "Due: ${checkup.dueDate} • Priority: ${checkup.urgency}",
          fontSize = 10.sp,
          color = MedicalTeal,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}
