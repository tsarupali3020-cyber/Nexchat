package com.example.ui.screens.calls

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.AddIcCall
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CallRecordEntity
import com.example.data.repository.ChatRepository
import com.example.service.VoiceCallManager
import com.example.ui.components.WhatsAppAvatar
import com.example.ui.theme.CallIncomingGreen
import com.example.ui.theme.CallMissedRed
import com.example.ui.theme.CallOutgoingGray
import com.example.ui.theme.WhatsAppGreenPrimary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallsScreen(
    repository: ChatRepository,
    callManager: VoiceCallManager,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val allCalls by repository.allCalls.collectAsState(initial = emptyList())
    val contacts by repository.allContacts.collectAsState(initial = emptyList())

    var showContactPicker by remember { mutableStateOf(false) }
    val timeFormat = remember { SimpleDateFormat("MMMM d, h:mm a", Locale.getDefault()) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Calls", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                actions = {
                    if (allCalls.isNotEmpty()) {
                        IconButton(onClick = { /* Clear calls */ }) {
                            Icon(imageVector = Icons.Default.ClearAll, contentDescription = "Clear History")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )

            if (allCalls.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "No Calls",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No recent voice calls",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp
                        )
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Text(
                            text = "Recent",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    items(allCalls, key = { it.id }) { call ->
                        CallRecordRow(
                            call = call,
                            timeFormatted = timeFormat.format(Date(call.timestamp)),
                            onCallBack = {
                                callManager.startOutgoingCall(
                                    contactName = call.contactName,
                                    contactPhone = call.contactPhone,
                                    contactAvatar = call.contactAvatar
                                )
                            }
                        )
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            modifier = Modifier.padding(start = 76.dp)
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showContactPicker = true },
            containerColor = WhatsAppGreenPrimary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AddIcCall,
                contentDescription = "New Call",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    // Contact Picker Dialog for Starting Voice Calls
    if (showContactPicker) {
        BasicAlertDialog(onDismissRequest = { showContactPicker = false }) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Start Voice Call",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = WhatsAppGreenPrimary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    contacts.forEach { contact ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showContactPicker = false
                                    callManager.startOutgoingCall(
                                        contactName = contact.name,
                                        contactPhone = contact.phone,
                                        contactAvatar = contact.avatarUrl
                                    )
                                }
                                .padding(vertical = 10.dp)
                        ) {
                            WhatsAppAvatar(name = contact.name, isOnline = contact.isOnline, size = 44.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = contact.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(text = contact.phone, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                            }
                            IconButton(onClick = {
                                showContactPicker = false
                                callManager.startOutgoingCall(
                                    contactName = contact.name,
                                    contactPhone = contact.phone,
                                    contactAvatar = contact.avatarUrl
                                )
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Call",
                                    tint = WhatsAppGreenPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { showContactPicker = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun CallRecordRow(
    call: CallRecordEntity,
    timeFormatted: String,
    onCallBack: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCallBack)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        WhatsAppAvatar(name = call.contactName, size = 48.dp)

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = call.contactName,
                fontWeight = FontWeight.Bold,
                fontSize = 15.5.sp,
                color = if (call.callType == "MISSED") CallMissedRed else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(3.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                when (call.callType) {
                    "MISSED" -> {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.CallMissed,
                            contentDescription = "Missed",
                            tint = CallMissedRed,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    "INCOMING" -> {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.CallReceived,
                            contentDescription = "Incoming",
                            tint = CallIncomingGreen,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.CallMade,
                            contentDescription = "Outgoing",
                            tint = CallOutgoingGray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = timeFormatted,
                    fontSize = 12.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        IconButton(onClick = onCallBack) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Callback",
                tint = WhatsAppGreenPrimary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
