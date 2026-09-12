package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.PrecisionRxViewModel
import com.example.ui.components.AppLockScreen
import com.example.ui.components.SafeHarborExportDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DietLifestyleScreen
import com.example.ui.screens.DrugInteractionsScreen
import com.example.ui.screens.GeneticsScreen
import com.example.ui.screens.HipaaComplianceScreen
import com.example.ui.screens.SecureMessagingScreen
import com.example.ui.screens.SymptomDiagnosticScreen
import com.example.ui.screens.VitalsLongitudinalScreen
import com.example.ui.theme.DNAViolet
import com.example.ui.theme.MedicalDarkBlue
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SeverityContraindicated
import com.example.ui.theme.SeveritySafe

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        PrecisionRxApp()
      }
    }
  }
}

enum class PrecisionNavDestination(
  val label: String,
  val icon: androidx.compose.ui.graphics.vector.ImageVector,
  val tag: String
) {
  DASHBOARD("Overview", Icons.Default.Dashboard, "nav_dashboard"),
  GENETICS("Genomics", Icons.Default.Biotech, "nav_genetics"),
  DRUGS("Drug Safety", Icons.Default.Medication, "nav_drugs"),
  SYMPTOMS("Screener", Icons.Default.Healing, "nav_symptoms"),
  TRENDS("Trends", Icons.Default.ShowChart, "nav_trends"),
  DIET("Diet & Care", Icons.Default.Restaurant, "nav_diet"),
  MESSAGES("Telehealth", Icons.Default.ChatBubbleOutline, "nav_messages"),
  HIPAA("HIPAA Vault", Icons.Default.Security, "nav_hipaa")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrecisionRxApp(
  viewModel: PrecisionRxViewModel = viewModel()
) {
  var currentDestination by remember { mutableIntStateOf(0) }

  // Observe ViewModel States
  val profile by viewModel.patientProfile.collectAsStateWithLifecycle()
  val markers by viewModel.geneticMarkers.collectAsStateWithLifecycle()
  val medications by viewModel.activeMedications.collectAsStateWithLifecycle()
  val symptomReports by viewModel.symptomReports.collectAsStateWithLifecycle()
  val trendPoints by viewModel.healthTrendPoints.collectAsStateWithLifecycle()
  val checkups by viewModel.checkupAlerts.collectAsStateWithLifecycle()
  val providerMessages by viewModel.providerMessages.collectAsStateWithLifecycle()
  val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
  val interactions by viewModel.drugInteractions.collectAsStateWithLifecycle()
  val riskPredictions by viewModel.diseaseRiskPredictions.collectAsStateWithLifecycle()
  val dietProtocols by viewModel.dietProtocols.collectAsStateWithLifecycle()

  val isAppLocked by viewModel.isAppLocked.collectAsStateWithLifecycle()
  val pinError by viewModel.pinError.collectAsStateWithLifecycle()
  val safeHarborExport by viewModel.safeHarborExport.collectAsStateWithLifecycle()
  val researchConsent by viewModel.researchConsentGranted.collectAsStateWithLifecycle()
  val providerSyncConsent by viewModel.providerTelemetrySync.collectAsStateWithLifecycle()

  // High-risk alerts count for badge
  val highRiskAlertCount = interactions.count {
    it.severity == com.example.data.model.EvaluatedInteraction.SeverityLevel.CONTRAINDICATED ||
      it.severity == com.example.data.model.EvaluatedInteraction.SeverityLevel.SEVERE
  }

  // App Lock Interceptor
  if (isAppLocked) {
    AppLockScreen(
      onUnlock = { pin -> viewModel.unlockWithPin(pin) },
      errorMessage = pinError
    )
    return
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .background(MedicalTeal, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Biotech,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "PrecisionRx",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .background(SeveritySafe, CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "HIPAA Encrypted • CPIC Level 1A",
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 10.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        },
        actions = {
          // Direct Telehealth shortcut
          IconButton(
            onClick = { currentDestination = PrecisionNavDestination.MESSAGES.ordinal },
            modifier = Modifier.testTag("top_bar_telehealth_button")
          ) {
            BadgedBox(
              badge = {
                Badge(containerColor = MedicalTeal) {
                  Text(providerMessages.size.toString(), color = Color.White)
                }
              }
            ) {
              Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Telehealth Messages", tint = MedicalTeal)
            }
          }

          // Lock App Button
          IconButton(
            onClick = { viewModel.lockApp() },
            modifier = Modifier.testTag("top_bar_lock_button")
          ) {
            Icon(Icons.Default.Lock, contentDescription = "Lock Protected Health Information", tint = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
      ) {
        val primaryNavs = listOf(
          PrecisionNavDestination.DASHBOARD,
          PrecisionNavDestination.GENETICS,
          PrecisionNavDestination.DRUGS,
          PrecisionNavDestination.SYMPTOMS,
          PrecisionNavDestination.TRENDS,
          PrecisionNavDestination.HIPAA
        )

        primaryNavs.forEach { dest ->
          val isSelected = currentDestination == dest.ordinal
          NavigationBarItem(
            selected = isSelected,
            onClick = { currentDestination = dest.ordinal },
            icon = {
              if (dest == PrecisionNavDestination.DRUGS && highRiskAlertCount > 0) {
                BadgedBox(
                  badge = {
                    Badge(containerColor = SeverityContraindicated) {
                      Text(highRiskAlertCount.toString(), color = Color.White)
                    }
                  }
                ) {
                  Icon(dest.icon, contentDescription = dest.label)
                }
              } else {
                Icon(dest.icon, contentDescription = dest.label)
              }
            },
            label = {
              Text(
                text = dest.label,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = MedicalTeal,
              selectedTextColor = MedicalTeal,
              indicatorColor = MedicalTeal.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag(dest.tag)
          )
        }
      }
    },
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Secondary Quick Navigation Sub-Bar (for fast access across all 8 modules)
      ScrollableTabRow(
        selectedTabIndex = currentDestination,
        edgePadding = 12.dp,
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        contentColor = MedicalTeal,
        modifier = Modifier.fillMaxWidth()
      ) {
        PrecisionNavDestination.values().forEachIndexed { index, dest ->
          Tab(
            selected = currentDestination == index,
            onClick = { currentDestination = index },
            text = {
              Text(
                text = dest.label,
                fontSize = 11.sp,
                fontWeight = if (currentDestination == index) FontWeight.Bold else FontWeight.Normal
              )
            }
          )
        }
      }

      // Screen router
      when (currentDestination) {
        PrecisionNavDestination.DASHBOARD.ordinal -> {
          DashboardScreen(
            profile = profile,
            markers = markers,
            interactions = interactions,
            medications = medications,
            trendPoints = trendPoints,
            checkups = checkups,
            onNavigateToGenetics = { currentDestination = PrecisionNavDestination.GENETICS.ordinal },
            onNavigateToInteractions = { currentDestination = PrecisionNavDestination.DRUGS.ordinal },
            onNavigateToSymptoms = { currentDestination = PrecisionNavDestination.SYMPTOMS.ordinal },
            onNavigateToTrends = { currentDestination = PrecisionNavDestination.TRENDS.ordinal },
            onNavigateToDiet = { currentDestination = PrecisionNavDestination.DIET.ordinal },
            onOpenSecurityCenter = { currentDestination = PrecisionNavDestination.HIPAA.ordinal },
            onUpdateDemographics = { age, gender, wt, ht, sys, dia, glu, chol, hist, allerg ->
              viewModel.updateDemographicsAndVitals(age, gender, wt, ht, sys, dia, glu, chol, hist, allerg)
            }
          )
        }
        PrecisionNavDestination.GENETICS.ordinal -> {
          GeneticsScreen(
            markers = markers,
            riskPredictions = riskPredictions
          )
        }
        PrecisionNavDestination.DRUGS.ordinal -> {
          DrugInteractionsScreen(
            medications = medications,
            interactions = interactions,
            onAddMedication = { name, dose, freq, reason ->
              viewModel.addMedication(name, dose, freq, reason)
            },
            onRemoveMedication = { id, name ->
              viewModel.removeMedication(id, name)
            }
          )
        }
        PrecisionNavDestination.SYMPTOMS.ordinal -> {
          SymptomDiagnosticScreen(
            symptomReports = symptomReports,
            onRunAssessment = { symptoms, onDone ->
              viewModel.runSymptomAssessment(symptoms, onDone)
            },
            onSendToProvider = { text, attTitle, attType ->
              viewModel.sendProviderMessage(text, attTitle, attType)
              currentDestination = PrecisionNavDestination.MESSAGES.ordinal
            }
          )
        }
        PrecisionNavDestination.TRENDS.ordinal -> {
          VitalsLongitudinalScreen(
            trendPoints = trendPoints,
            onAddVitalLog = { sys, dia, glu, wt, chol ->
              viewModel.updateDemographicsAndVitals(
                profile?.age ?: 42,
                profile?.gender ?: "Female",
                wt,
                profile?.heightCm ?: 168f,
                sys,
                dia,
                glu,
                chol,
                profile?.medicalHistory ?: "",
                profile?.allergies ?: ""
              )
            }
          )
        }
        PrecisionNavDestination.DIET.ordinal -> {
          DietLifestyleScreen(
            dietProtocols = dietProtocols,
            checkups = checkups,
            onToggleCheckup = { id, done ->
              viewModel.toggleCheckup(id, done)
            }
          )
        }
        PrecisionNavDestination.MESSAGES.ordinal -> {
          SecureMessagingScreen(
            messages = providerMessages,
            onSendMessage = { text, attTitle, attType ->
              viewModel.sendProviderMessage(text, attTitle, attType)
            }
          )
        }
        PrecisionNavDestination.HIPAA.ordinal -> {
          HipaaComplianceScreen(
            auditLogs = auditLogs,
            researchConsent = researchConsent,
            providerSyncConsent = providerSyncConsent,
            onToggleResearchConsent = { viewModel.toggleResearchConsent(it) },
            onToggleProviderSyncConsent = { viewModel.toggleProviderSync(it) },
            onLockAppNow = { viewModel.lockApp() },
            onGenerateSafeHarborExport = { viewModel.generateSafeHarborExport() }
          )
        }
      }
    }
  }

  // Safe Harbor Anonymized Export Modal Dialog
  safeHarborExport?.let { exportResult ->
    SafeHarborExportDialog(
      exportResult = exportResult,
      onDismiss = { viewModel.clearExport() }
    )
  }
}
