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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HipaaAuditLogEntity
import com.example.ui.theme.DNAViolet
import com.example.ui.theme.MedicalCyan
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.SeveritySafe
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HipaaComplianceScreen(
  auditLogs: List<HipaaAuditLogEntity>,
  researchConsent: Boolean,
  providerSyncConsent: Boolean,
  onToggleResearchConsent: (Boolean) -> Unit,
  onToggleProviderSyncConsent: (Boolean) -> Unit,
  onLockAppNow: () -> Unit,
  onGenerateSafeHarborExport: () -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("hipaa_compliance_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .background(SeveritySafe.copy(alpha = 0.15f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SeveritySafe, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "HIPAA Security Architecture",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Health Insurance Portability and Accountability Act",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Key Safeguards Grid
          val safeguards = listOf(
            "Physical/Technical" to "AES-256-GCM Hardware Keystore",
            "In-Transit Security" to "TLS 1.3 End-to-End Encryption",
            "Safe Harbor Rule" to "45 CFR § 164.514(b) (18 Purged Identifiers)",
            "Access Controls" to "Role-Based & Biometric Authentication"
          )

          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            safeguards.forEach { (title, desc) ->
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SeveritySafe, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(text = title, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                  Text(text = desc, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
              }
            }
          }
        }
      }
    }

    // 2. Patient Privacy Controls & Consents
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Patient Consent & Access Controls",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(12.dp))

          // Lock App Action
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Biometric / PIN Session Lock", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
              Text("Lock Protected Health Information (PHI) immediately", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            OutlinedButton(
              onClick = onLockAppNow,
              modifier = Modifier.testTag("lock_app_now_button")
            ) {
              Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Lock Now", fontSize = 11.sp)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Genomic Research Data Consent
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("De-Identified Genomic Research", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
              Text("Permit anonymized variant epidemiology sharing", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
              checked = researchConsent,
              onCheckedChange = onToggleResearchConsent,
              colors = SwitchDefaults.colors(checkedThumbColor = MedicalTeal, checkedTrackColor = MedicalTeal.copy(alpha = 0.5f))
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Provider Telemetry Sync
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Provider Telemetry Sync", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
              Text("Transmit daily vitals to Dr. Chen's oncology team", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
              checked = providerSyncConsent,
              onCheckedChange = onToggleProviderSyncConsent,
              colors = SwitchDefaults.colors(checkedThumbColor = MedicalTeal, checkedTrackColor = MedicalTeal.copy(alpha = 0.5f))
            )
          }
        }
      }
    }

    // 3. Safe Harbor Export Generator CTA
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Shield, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Safe Harbor De-Identification Exporter",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Generate a mathematically anonymized JSON health record compliant with 45 CFR § 164.514(b) with all 18 direct identifiers scrubbed.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp
          )
          Spacer(modifier = Modifier.height(10.dp))
          Button(
            onClick = onGenerateSafeHarborExport,
            colors = ButtonDefaults.buttonColors(containerColor = MedicalTeal),
            modifier = Modifier.testTag("generate_safe_harbor_button")
          ) {
            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Generate Safe Harbor Export")
          }
        }
      }
    }

    // 4. Live Tamper-Proof Audit Trail
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.History, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "HIPAA Immutable Audit Trail (${auditLogs.size})",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )
        }
      }
    }

    items(auditLogs.reversed().take(15)) { log ->
      AuditLogItem(log = log)
    }

    item { Spacer(modifier = Modifier.height(24.dp)) }
  }
}

@Composable
fun AuditLogItem(log: HipaaAuditLogEntity) {
  val sdf = SimpleDateFormat("MMM d, HH:mm:ss", Locale.US)
  val dateStr = sdf.format(Date(log.timestamp))

  Card(
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .background(MedicalTeal.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text(
              text = log.action,
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = MedicalTeal
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = log.operator,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = log.targetResource,
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 2
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = dateStr,
          fontSize = 9.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = log.securityHash.take(10) + "…",
          fontSize = 8.sp,
          fontFamily = FontFamily.Monospace,
          color = MedicalTeal
        )
      }
    }
  }
}
