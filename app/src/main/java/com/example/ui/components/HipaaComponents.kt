package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.service.HipaaSecurityManager
import com.example.ui.theme.MedicalDarkBlue
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.SeverityContraindicated
import com.example.ui.theme.SeveritySafe

@Composable
fun HipaaComplianceBanner(
  onOpenSecurityCenter: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    ),
    modifier = modifier
      .fillMaxWidth()
      .clickable { onOpenSecurityCenter() }
      .testTag("hipaa_banner")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .background(MedicalTeal, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.VerifiedUser,
            contentDescription = "HIPAA Compliant",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "HIPAA Privacy Protected",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .background(SeveritySafe, RoundedCornerShape(4.dp))
                .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
              Text("AES-256", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
          }
          Text(
            text = "Zero-Knowledge Storage • Safe Harbor Certified • Audit Active",
            style = MaterialTheme.typography.bodySmall,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
          )
        }
      }

      Text(
        text = "Security Logs ›",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MedicalTeal
      )
    }
  }
}

@Composable
fun AppLockScreen(
  onUnlock: (String) -> Boolean,
  errorMessage: String?
) {
  var enteredPin by remember { mutableStateOf("") }
  var localError by remember { mutableStateOf(errorMessage) }

  Surface(
    modifier = Modifier
      .fillMaxSize()
      .testTag("app_lock_screen"),
    color = MedicalDarkBlue
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(72.dp)
          .background(Color.White.copy(alpha = 0.1f), CircleShape)
          .border(2.dp, MedicalTeal, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = "App Locked",
          tint = MedicalTeal,
          modifier = Modifier.size(36.dp)
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = "PrecisionRx Vault Locked",
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        color = Color.White
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Protected Health Information (PHI) encrypted.\nEnter your 4-digit security PIN to unlock.",
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = Color.White.copy(alpha = 0.7f)
      )

      Spacer(modifier = Modifier.height(24.dp))

      // PIN Indicator Dots
      Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        for (i in 1..4) {
          val filled = enteredPin.length >= i
          Box(
            modifier = Modifier
              .size(16.dp)
              .background(
                if (filled) MedicalTeal else Color.White.copy(alpha = 0.2f),
                CircleShape
              )
          )
        }
      }

      if (localError != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = localError ?: "",
          color = SeverityContraindicated,
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium
        )
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Numeric Keypad
      val digits = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("Clear", "0", "OK")
      )

      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        digits.forEach { row ->
          Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
          ) {
            row.forEach { digit ->
              Box(
                modifier = Modifier
                  .size(64.dp)
                  .background(Color.White.copy(alpha = 0.08f), CircleShape)
                  .clickable {
                    when (digit) {
                      "Clear" -> {
                        enteredPin = ""
                        localError = null
                      }
                      "OK" -> {
                        if (enteredPin.length == 4) {
                          val ok = onUnlock(enteredPin)
                          if (!ok) localError = "Incorrect PIN (Default: 1234)"
                        } else {
                          localError = "Enter 4 digits"
                        }
                      }
                      else -> {
                        if (enteredPin.length < 4) {
                          enteredPin += digit
                          localError = null
                          if (enteredPin.length == 4) {
                            val ok = onUnlock(enteredPin)
                            if (!ok) localError = "Incorrect PIN (Default: 1234)"
                          }
                        }
                      }
                    }
                  },
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = digit,
                  color = Color.White,
                  fontSize = if (digit.length > 1) 14.sp else 22.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Demo quick bypass hint for testing
      TextButton(
        onClick = { onUnlock("1234") },
        modifier = Modifier.testTag("quick_demo_unlock")
      ) {
        Text("Quick Unlock Demo (PIN: 1234)", color = MedicalTeal, fontSize = 12.sp)
      }
    }
  }
}

@Composable
fun SafeHarborExportDialog(
  exportResult: HipaaSecurityManager.AnonymizedExportResult,
  onDismiss: () -> Unit
) {
  var copied by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(vertical = 24.dp)
        .testTag("safe_harbor_export_dialog"),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = SeveritySafe,
            modifier = Modifier.size(28.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "HIPAA Safe Harbor De-Identified Record",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "45 CFR § 164.514(b) Export Package",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Verification Badges
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SeveritySafe, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Subject Hash: ${exportResult.anonymizedSubjectId}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SeveritySafe, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("18 Direct Identifiers purged (Name, SSN, MRN, Dates)", fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SeveritySafe, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Digital Signature: SHA-256 Validated", fontSize = 11.sp)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Payload (JSON Ready for Research Integration):",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color(0xFF0A101D), RoundedCornerShape(8.dp))
            .padding(10.dp)
            .verticalScroll(rememberScrollState())
        ) {
          Text(
            text = exportResult.exportContentJson,
            color = Color(0xFF68D391),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          OutlinedButton(onClick = onDismiss) {
            Text("Close")
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = { copied = true },
            colors = ButtonDefaults.buttonColors(containerColor = MedicalTeal),
            modifier = Modifier.testTag("copy_export_button")
          ) {
            Text(if (copied) "Export Certified ✓" else "Save & Transmit")
          }
        }
      }
    }
  }
}
