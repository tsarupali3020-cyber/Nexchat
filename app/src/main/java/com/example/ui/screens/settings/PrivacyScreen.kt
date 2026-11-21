package com.example.ui.screens.settings

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ChatRepository
import com.example.ui.theme.WhatsAppGreenPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(
    repository: ChatRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val account by repository.userAccount.collectAsState(initial = null)
    val contacts by repository.allContacts.collectAsState(initial = emptyList())

    var showVisibilityDialogFor by remember { mutableStateOf<String?>(null) } // "LAST_SEEN" or "PROFILE_PHOTO"

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        TopAppBar(
            title = { Text("Privacy", fontWeight = FontWeight.Bold) },
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
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Who can see my personal info",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhatsAppGreenPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Last seen & Online
                PrivacySettingItem(
                    title = "Last seen and online",
                    subtitle = account?.lastSeenVisibility?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Everyone",
                    icon = Icons.Default.Visibility,
                    onClick = { showVisibilityDialogFor = "LAST_SEEN" }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                // Profile photo
                PrivacySettingItem(
                    title = "Profile photo",
                    subtitle = account?.profilePhotoVisibility?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Everyone",
                    icon = Icons.Default.Image,
                    onClick = { showVisibilityDialogFor = "PROFILE_PHOTO" }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                // Read Receipts Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Icon(imageVector = Icons.Default.DoneAll, contentDescription = "Receipts", tint = WhatsAppGreenPrimary)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Read receipts", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = "If turned off, you won't send or receive read receipts (blue double ticks).",
                            fontSize = 12.5.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = account?.readReceiptsEnabled ?: true,
                        onCheckedChange = { enabled ->
                            scope.launch {
                                repository.updateAccount { it.copy(readReceiptsEnabled = enabled) }
                            }
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = WhatsAppGreenPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Disappearing & Blocked",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhatsAppGreenPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                val blockedCount = contacts.count { it.isBlocked }
                PrivacySettingItem(
                    title = "Blocked contacts",
                    subtitle = if (blockedCount == 0) "None" else "$blockedCount contacts",
                    icon = Icons.Default.Block,
                    onClick = {}
                )

                if (blockedCount > 0) {
                    contacts.filter { it.isBlocked }.forEach { contact ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(start = 38.dp, top = 4.dp, bottom = 4.dp)
                        ) {
                            Text(text = contact.name, modifier = Modifier.weight(1f), fontSize = 14.sp)
                            TextButton(onClick = {
                                scope.launch {
                                    repository.toggleBlockContact(contact.id, true)
                                    Toast.makeText(context, "${contact.name} unblocked", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Text("Unblock", color = WhatsAppGreenPrimary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // Visibility Selector Dialog
    showVisibilityDialogFor?.let { target ->
        val isLastSeen = target == "LAST_SEEN"
        val currentVal = if (isLastSeen) account?.lastSeenVisibility ?: "EVERYONE" else account?.profilePhotoVisibility ?: "EVERYONE"
        val options = listOf("EVERYONE" to "Everyone", "CONTACTS" to "My contacts", "NOBODY" to "Nobody")

        BasicAlertDialog(onDismissRequest = { showVisibilityDialogFor = null }) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (isLastSeen) "Who can see my last seen" else "Who can see my profile photo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = WhatsAppGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    options.forEach { (code, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch {
                                        repository.updateAccount { acc ->
                                            if (isLastSeen) acc.copy(lastSeenVisibility = code)
                                            else acc.copy(profilePhotoVisibility = code)
                                        }
                                        showVisibilityDialogFor = null
                                    }
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = currentVal == code,
                                onClick = {
                                    scope.launch {
                                        repository.updateAccount { acc ->
                                            if (isLastSeen) acc.copy(lastSeenVisibility = code)
                                            else acc.copy(profilePhotoVisibility = code)
                                        }
                                        showVisibilityDialogFor = null
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = label, fontSize = 15.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = { showVisibilityDialogFor = null },
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
fun PrivacySettingItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = WhatsAppGreenPrimary)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(text = subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
