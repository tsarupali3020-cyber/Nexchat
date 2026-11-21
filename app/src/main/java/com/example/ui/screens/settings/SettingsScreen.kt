package com.example.ui.screens.settings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ChatRepository
import com.example.ui.components.WhatsAppAvatar
import com.example.ui.theme.MetaAiBlue
import com.example.ui.theme.WhatsAppGreenPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    repository: ChatRepository,
    onNavigateToLinkedDevices: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToAi: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val account by repository.userAccount.collectAsState(initial = null)

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editBio by remember { mutableStateOf("") }
    var showMyQrDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Settings", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // Profile Card
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            editName = account?.username ?: "Alex Morgan"
                            editBio = account?.statusBio ?: "Available"
                            showEditProfileDialog = true
                        }
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    WhatsAppAvatar(
                        name = account?.username ?: "Alex Morgan",
                        size = 62.dp
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = account?.username ?: "Alex Morgan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = account?.statusBio ?: "Hey there! I am using NexChat.",
                            fontSize = 13.5.sp,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = account?.phoneNumber ?: "+1 (555) 234-5678",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }

                    IconButton(onClick = { showMyQrDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "My QR Code",
                            tint = WhatsAppGreenPrimary
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Setting Options
            item {
                SettingNavigationRow(
                    icon = Icons.Default.Key,
                    title = "Account & Security",
                    subtitle = "Security notifications, two-step verification, shutdown protocol",
                    onClick = onNavigateToSecurity
                )

                SettingNavigationRow(
                    icon = Icons.Default.Lock,
                    title = "Privacy",
                    subtitle = "Block contacts, disappearing messages, read receipts",
                    onClick = onNavigateToPrivacy
                )

                SettingNavigationRow(
                    icon = Icons.Default.Devices,
                    title = "Linked Devices",
                    subtitle = "Desktop app pairing, Web sessions, remote logout",
                    onClick = onNavigateToLinkedDevices
                )

                SettingNavigationRow(
                    icon = Icons.Default.AutoAwesome,
                    title = "NexAI Assistant",
                    subtitle = "Meta AI-inspired chat, summarization, message drafting",
                    iconTint = MetaAiBlue,
                    onClick = onNavigateToAi
                )

                SettingNavigationRow(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Message, group & call ringtones",
                    onClick = {}
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Footer branding
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Text(
                        text = "from",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "NexChat • AI & Secure Messaging",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = WhatsAppGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Protected with 256-bit End-to-End Encryption",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        BasicAlertDialog(onDismissRequest = { showEditProfileDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Edit Profile", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = WhatsAppGreenPrimary)
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Your Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("About / Bio") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.align(Alignment.End)) {
                        TextButton(onClick = { showEditProfileDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                if (editName.isNotBlank()) {
                                    scope.launch {
                                        repository.updateAccount { it.copy(username = editName.trim(), statusBio = editBio.trim()) }
                                        showEditProfileDialog = false
                                    }
                                }
                            }
                        ) {
                            Text("Save", color = WhatsAppGreenPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // My Contact QR Code Dialog
    if (showMyQrDialog) {
        BasicAlertDialog(onDismissRequest = { showMyQrDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    WhatsAppAvatar(name = account?.username ?: "Alex Morgan", size = 64.dp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = account?.username ?: "Alex Morgan", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = account?.phoneNumber ?: "+1 (555) 234-5678", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(20.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(160.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "QR Code",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(110.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your QR code is private. If you share it with someone, they can scan it with their NexChat camera to message you.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { showMyQrDialog = false }) {
                        Text("Close", color = WhatsAppGreenPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingNavigationRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color = WhatsAppGreenPrimary,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(18.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.5.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 12.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
