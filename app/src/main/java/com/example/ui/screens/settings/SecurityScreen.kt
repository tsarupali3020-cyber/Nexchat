package com.example.ui.screens.settings

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ChatRepository
import com.example.ui.theme.WhatsAppGreenAccent
import com.example.ui.theme.WhatsAppGreenPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    repository: ChatRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val account by repository.userAccount.collectAsState(initial = null)

    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var showLockdownTestDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        TopAppBar(
            title = { Text("Security & Encryption", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // End-to-End Encryption Hero Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = WhatsAppGreenPrimary.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = WhatsAppGreenPrimary,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Lock",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "256-bit End-to-End Encryption",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = WhatsAppGreenPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Your messages, voice notes, photos, documents, and voice calls are secured with end-to-end encryption. Only you and the recipient have the unique keys.",
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Account Security Controls",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = WhatsAppGreenPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Two-Step Verification
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Key, contentDescription = "2FA", tint = WhatsAppGreenPrimary)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Two-step verification", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = if (account?.isTwoFactorEnabled == true) "Enabled (PIN protected)" else "Disabled",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = account?.isTwoFactorEnabled == true,
                        onCheckedChange = { enabled ->
                            if (enabled) {
                                showPinDialog = true
                            } else {
                                scope.launch {
                                    repository.updateAccount { it.copy(isTwoFactorEnabled = false, twoFactorPin = "") }
                                }
                            }
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = WhatsAppGreenPrimary)
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                // Biometric App Lock
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Fingerprint, contentDescription = "Biometrics", tint = WhatsAppGreenPrimary)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Fingerprint / Biometric Lock", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = "Require biometric sensor to unlock NexChat",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = account?.isBiometricEnabled == true,
                        onCheckedChange = { enabled ->
                            scope.launch {
                                repository.updateAccount { it.copy(isBiometricEnabled = enabled) }
                            }
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = WhatsAppGreenPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Anti-Hacking & Intrusion Defense Section
                Text(
                    text = "Intrusion Detection & Auto-Shutdown",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = WhatsAppGreenPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = "Shield", tint = WhatsAppGreenAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Real-Time Defense Status: ACTIVE", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• Rate limiting on authentication\n• Session hijacking monitoring\n• Automatic session revocation\n• Zero data deletion guarantee on suspected attack",
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { showLockdownTestDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = "Test Lockdown")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simulate Security Shutdown Protocol")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // Set 2FA PIN Dialog
    if (showPinDialog) {
        BasicAlertDialog(onDismissRequest = { showPinDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Set Two-Step Verification PIN", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = WhatsAppGreenPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Enter a 6-digit PIN which will be required when registering your phone number again:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { if (it.length <= 6 && it.all { char -> char.isDigit() }) pinInput = it },
                        placeholder = { Text("6-digit PIN") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.align(Alignment.End)) {
                        TextButton(onClick = { showPinDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                if (pinInput.length == 6) {
                                    scope.launch {
                                        repository.updateAccount { it.copy(isTwoFactorEnabled = true, twoFactorPin = pinInput) }
                                        showPinDialog = false
                                        pinInput = ""
                                        Toast.makeText(context, "Two-step verification enabled!", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Please enter 6 digits", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text("Save PIN", color = WhatsAppGreenPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Simulate Lockdown Test Dialog
    if (showLockdownTestDialog) {
        BasicAlertDialog(onDismissRequest = { showLockdownTestDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Test Security Shutdown Protocol", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This simulates the anti-hacking response per specification:\n\n" +
                                "1. Instantly revokes all remote linked device sessions.\n" +
                                "2. Temporarily locks the account.\n" +
                                "3. Blocks unauthorized access.\n" +
                                "4. Preserves all messages and contacts safely (never deletes data).\n" +
                                "5. Requires security OTP verification to restore access.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.align(Alignment.End)) {
                        TextButton(onClick = { showLockdownTestDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                showLockdownTestDialog = false
                                scope.launch {
                                    repository.triggerSecurityLockdown(
                                        "Suspicious concurrent access attempt detected from unauthorized IP."
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Execute Shutdown")
                        }
                    }
                }
            }
        }
    }
}
