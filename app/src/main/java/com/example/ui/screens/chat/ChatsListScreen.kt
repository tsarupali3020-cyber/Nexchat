package com.example.ui.screens.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ChatEntity
import com.example.data.repository.ChatRepository
import com.example.service.VoiceCallManager
import com.example.ui.components.WhatsAppAvatar
import com.example.ui.components.WhatsAppTicks
import com.example.ui.theme.MetaAiBlue
import com.example.ui.theme.WhatsAppGreenAccent
import com.example.ui.theme.WhatsAppGreenPrimary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsListScreen(
    repository: ChatRepository,
    callManager: VoiceCallManager,
    onChatSelected: (chatId: String) -> Unit,
    onNavigateToAi: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToContacts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val allChats by repository.allChats.collectAsState(initial = emptyList())

    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var showNewChatDialog by remember { mutableStateOf(false) }
    var newChatName by remember { mutableStateOf("") }
    var newChatPhone by remember { mutableStateOf("") }
    var isGroupNewChat by remember { mutableStateOf(false) }

    val filteredChats = allChats.filter {
        !it.isArchived && (searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) || it.lastMessageText.contains(searchQuery, ignoreCase = true))
    }

    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // WhatsApp Header
            TopAppBar(
                title = {
                    if (isSearching) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search chats...", fontSize = 15.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        )
                    } else {
                        Text(
                            text = "NexChat",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = WhatsAppGreenPrimary
                        )
                    }
                },
                actions = {
                    if (isSearching) {
                        IconButton(onClick = {
                            isSearching = false
                            searchQuery = ""
                        }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close Search")
                        }
                    } else {
                        IconButton(onClick = onNavigateToAi) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "NexAI Assistant",
                                tint = MetaAiBlue
                            )
                        }

                        IconButton(onClick = { isSearching = true }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        }

                        IconButton(onClick = {
                            // Demo incoming call trigger
                            callManager.triggerIncomingCall(
                                contactName = "Sarah Jenkins",
                                contactPhone = "+1 (555) 019-2834",
                                contactAvatar = ""
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Default.PhoneInTalk,
                                contentDescription = "Test Incoming Call",
                                tint = WhatsAppGreenAccent
                            )
                        }

                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("New group") },
                                    onClick = {
                                        showMenu = false
                                        isGroupNewChat = true
                                        showNewChatDialog = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("New chat") },
                                    onClick = {
                                        showMenu = false
                                        isGroupNewChat = false
                                        showNewChatDialog = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Contacts (Manage & Delete)") },
                                    onClick = {
                                        showMenu = false
                                        onNavigateToContacts()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Settings") },
                                    onClick = {
                                        showMenu = false
                                        onNavigateToSettings()
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Chats List
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredChats, key = { it.id }) { chat ->
                    ChatItemRow(
                        chat = chat,
                        timeFormatted = timeFormat.format(Date(chat.lastMessageTimestamp)),
                        onClick = { onChatSelected(chat.id) },
                        onTogglePin = { scope.launch { repository.togglePin(chat.id, chat.isPinned) } },
                        onToggleArchive = { scope.launch { repository.toggleArchive(chat.id, chat.isArchived) } }
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

        // Floating Action Button
        FloatingActionButton(
            onClick = {
                isGroupNewChat = false
                showNewChatDialog = true
            },
            containerColor = WhatsAppGreenPrimary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = "New Chat",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    // New Chat / New Group Dialog
    if (showNewChatDialog) {
        AlertDialog(
            onDismissRequest = { showNewChatDialog = false },
            title = {
                Text(
                    text = if (isGroupNewChat) "Create New Group" else "Start New Chat",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = newChatName,
                        onValueChange = { newChatName = it },
                        label = { Text(if (isGroupNewChat) "Group Name" else "Contact Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newChatPhone,
                        onValueChange = { newChatPhone = it },
                        label = { Text("Phone Number") },
                        placeholder = { Text("+1 (555) 000-0000") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = {
                            showNewChatDialog = false
                            onNavigateToContacts()
                        }
                    ) {
                        Text("👤 Select or Manage Contacts", color = WhatsAppGreenPrimary, fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val name = newChatName.trim()
                        if (name.isNotBlank()) {
                            scope.launch {
                                val id = repository.createNewChat(name, newChatPhone, isGroupNewChat)
                                showNewChatDialog = false
                                newChatName = ""
                                newChatPhone = ""
                                onChatSelected(id)
                            }
                        }
                    }
                ) {
                    Text("Create", color = WhatsAppGreenPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewChatDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ChatItemRow(
    chat: ChatEntity,
    timeFormatted: String,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleArchive: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        WhatsAppAvatar(
            name = chat.name,
            isGroup = chat.isGroup,
            isAiChat = chat.isAiChat,
            isOnline = chat.isOnline,
            size = 50.dp
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = chat.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )

                Text(
                    text = timeFormatted,
                    fontSize = 12.sp,
                    color = if (chat.unreadCount > 0) WhatsAppGreenAccent else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = chat.lastMessageText,
                    fontSize = 13.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                if (chat.isPinned) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp).padding(end = 4.dp)
                    )
                }

                if (chat.unreadCount > 0) {
                    Badge(
                        containerColor = WhatsAppGreenAccent,
                        contentColor = Color.White
                    ) {
                        Text(text = chat.unreadCount.toString(), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
