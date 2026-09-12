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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.model.ProviderMessageEntity
import com.example.ui.theme.DNAViolet
import com.example.ui.theme.MedicalDarkBlue
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.SeveritySafe
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SecureMessagingScreen(
  messages: List<ProviderMessageEntity>,
  onSendMessage: (String, String?, String?) -> Unit,
  modifier: Modifier = Modifier
) {
  var textInput by remember { mutableStateOf("") }
  var selectedAttachment by remember { mutableStateOf<Pair<String, String>?>(null) }
  val listState = rememberLazyListState()

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("secure_messaging_screen")
  ) {
    // 1. Healthcare Provider Banner
    Card(
      shape = RoundedCornerShape(0.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .background(MedicalTeal, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Dr. Sarah Chen, MD",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Medical Geneticist & Precision Oncologist",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Box(
            modifier = Modifier
              .background(SeveritySafe.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(modifier = Modifier.size(6.dp).background(SeveritySafe, CircleShape))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Active", fontSize = 10.sp, color = SeveritySafe, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // HIPAA E2EE Status Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Lock, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "HIPAA End-to-End Encrypted (TLS 1.3 • AES-256 Storage)",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // 2. Chat Bubble Stream
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      item { Spacer(modifier = Modifier.height(8.dp)) }

      items(messages) { msg ->
        MessageBubble(message = msg)
      }

      item { Spacer(modifier = Modifier.height(8.dp)) }
    }

    // 3. Quick Attachments Bar
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 3.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Attach PHI:",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          val attachments = listOf(
            "Genomic Summary" to "GENOMIC_REPORT",
            "Symptom Report" to "SYMPTOM_REPORT",
            "Vitals Log" to "VITALS_LOG"
          )

          attachments.forEach { (label, type) ->
            val isSelected = selectedAttachment?.second == type
            FilterChip(
              selected = isSelected,
              onClick = {
                selectedAttachment = if (isSelected) null else (label to type)
              },
              label = { Text(label, fontSize = 10.sp) },
              leadingIcon = {
                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(12.dp))
              }
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Message Input Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            placeholder = { Text("Secure message to Dr. Chen...", fontSize = 13.sp) },
            modifier = Modifier
              .weight(1f)
              .testTag("message_input_field"),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = MedicalTeal,
              unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            maxLines = 3
          )

          Spacer(modifier = Modifier.width(8.dp))

          IconButton(
            onClick = {
              if (textInput.isNotBlank() || selectedAttachment != null) {
                val attTitle = selectedAttachment?.first
                val attType = selectedAttachment?.second
                val msgToSend = if (textInput.isBlank() && attTitle != null) "Transmitting $attTitle for review." else textInput
                onSendMessage(msgToSend, attTitle, attType)
                textInput = ""
                selectedAttachment = null
              }
            },
            modifier = Modifier
              .size(48.dp)
              .background(MedicalTeal, CircleShape)
              .testTag("send_message_button")
          ) {
            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
          }
        }
      }
    }
  }
}

@Composable
fun MessageBubble(message: ProviderMessageEntity) {
  val isUser = message.isFromUser
  val sdf = SimpleDateFormat("h:mm a", Locale.US)
  val timeStr = sdf.format(Date(message.timestamp))

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
  ) {
    Card(
      shape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isUser) 16.dp else 4.dp,
        bottomEnd = if (isUser) 4.dp else 16.dp
      ),
      colors = CardDefaults.cardColors(
        containerColor = if (isUser) MedicalTeal else MaterialTheme.colorScheme.surfaceVariant
      ),
      modifier = Modifier.fillMaxWidth(0.82f)
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        if (!isUser) {
          Text(
            text = message.providerName,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = MedicalTeal
          )
          Spacer(modifier = Modifier.height(2.dp))
        }

        // Optional attached clinical payload badge
        if (message.attachmentTitle != null) {
          Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isUser) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 6.dp)
          ) {
            Row(
              modifier = Modifier.padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                Icons.Default.Description,
                contentDescription = null,
                tint = if (isUser) Color.White else MedicalTeal,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Attached: ${message.attachmentTitle}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }

        Text(
          text = message.text,
          fontSize = 13.sp,
          color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (message.isEncrypted) {
            Icon(
              Icons.Default.Lock,
              contentDescription = "Encrypted",
              tint = if (isUser) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
          }
          Text(
            text = timeStr,
            fontSize = 10.sp,
            color = if (isUser) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}
