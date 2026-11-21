package com.example.ui.screens.chat

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ChatEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.repository.AiAssistantService
import com.example.data.repository.ChatRepository
import com.example.service.VoiceCallManager
import com.example.ui.components.ChatInputBar
import com.example.ui.components.MessageActionDialog
import com.example.ui.components.MessageBubble
import com.example.ui.components.WhatsAppAvatar
import com.example.ui.theme.DarkChatBackground
import com.example.ui.theme.LightChatBackground
import com.example.ui.theme.MetaAiBlue
import com.example.ui.theme.MetaAiCyan
import com.example.ui.theme.WhatsAppGreenPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    chatId: String,
    repository: ChatRepository,
    aiService: AiAssistantService,
    callManager: VoiceCallManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()

    val chat by repository.getChat(chatId).collectAsState(initial = null)
    val messages by repository.getMessages(chatId).collectAsState(initial = emptyList())
    val allChats by repository.allChats.collectAsState(initial = emptyList())

    var inputText by remember { mutableStateOf("") }
    var replyingToMessage by remember { mutableStateOf<MessageEntity?>(null) }
    var selectedActionMessage by remember { mutableStateOf<MessageEntity?>(null) }
    var messageToEdit by remember { mutableStateOf<MessageEntity?>(null) }
    var editTextValue by remember { mutableStateOf("") }
    var messageToForward by remember { mutableStateOf<MessageEntity?>(null) }

    var showMenu by remember { mutableStateOf(false) }
    var showAiSummaryDialog by remember { mutableStateOf(false) }
    var aiSummaryContent by remember { mutableStateOf("") }
    var isGeneratingSummary by remember { mutableStateOf(false) }
    var showEncryptionDialog by remember { mutableStateOf(false) }
    var showDeleteContactDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val currentChat = chat ?: ChatEntity(
        id = chatId,
        name = "Chat",
        lastSeenText = "online"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) DarkChatBackground else LightChatBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        // WhatsApp Conversation Header TopAppBar
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showEncryptionDialog = true }
                ) {
                    WhatsAppAvatar(
                        name = currentChat.name,
                        isGroup = currentChat.isGroup,
                        isAiChat = currentChat.isAiChat,
                        isOnline = currentChat.isOnline,
                        size = 38.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = currentChat.name,
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = currentChat.lastSeenText,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            actions = {
                if (!currentChat.isAiChat) {
                    IconButton(
                        onClick = {
                            callManager.startOutgoingCall(
                                contactName = currentChat.name,
                                contactPhone = "+1 555-0192",
                                contactAvatar = currentChat.avatarUrl
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Voice Call",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "AI",
                                        tint = MetaAiBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Summarize with AI")
                                }
                            },
                            onClick = {
                                showMenu = false
                                isGeneratingSummary = true
                                showAiSummaryDialog = true
                                scope.launch {
                                    aiSummaryContent = aiService.summarizeChat(messages, currentChat.name)
                                    isGeneratingSummary = false
                                }
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Encryption",
                                        tint = WhatsAppGreenPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Verify Encryption")
                                }
                            },
                            onClick = {
                                showMenu = false
                                showEncryptionDialog = true
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Clear Chat") },
                            onClick = {
                                showMenu = false
                                scope.launch { repository.clearChat(chatId) }
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Contact",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Delete Contact / Chat", color = MaterialTheme.colorScheme.error)
                                }
                            },
                            onClick = {
                                showMenu = false
                                showDeleteContactDialog = true
                            }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Messages list
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = 8.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // End-to-End Encryption notice card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        modifier = Modifier.clickable { showEncryptionDialog = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Security",
                                tint = WhatsAppGreenPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Messages and calls are end-to-end encrypted. No one outside of this chat, not even NexChat, can read or listen to them. Tap to verify.",
                                fontSize = 11.sp,
                                lineHeight = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            items(messages, key = { it.id }) { message ->
                MessageBubble(
                    message = message,
                    isDark = isDark,
                    onLongPress = { selectedActionMessage = message }
                )
            }
        }

        // Composer Input Bar
        ChatInputBar(
            text = inputText,
            onTextChanged = { inputText = it },
            onSendText = {
                val toSend = inputText.trim()
                if (toSend.isNotBlank()) {
                    val rep = replyingToMessage
                    inputText = ""
                    replyingToMessage = null
                    scope.launch {
                        repository.sendMessage(
                            chatId = chatId,
                            content = toSend,
                            replyTo = rep
                        )
                    }
                }
            },
            onSendVoiceNote = { duration ->
                scope.launch {
                    repository.sendMessage(
                        chatId = chatId,
                        content = "Voice note",
                        messageType = "VOICE",
                        mediaDurationSec = duration
                    )
                }
            },
            onSendAttachment = { type, filename ->
                scope.launch {
                    repository.sendMessage(
                        chatId = chatId,
                        content = filename,
                        messageType = type
                    )
                }
            },
            replyingTo = replyingToMessage,
            onCancelReply = { replyingToMessage = null },
            isDark = isDark
        )
    }

    // Message Actions Menu (Reactions, Reply, Forward, Edit, Delete)
    selectedActionMessage?.let { msg ->
        MessageActionDialog(
            message = msg,
            onDismiss = { selectedActionMessage = null },
            onReact = { emoji ->
                scope.launch { repository.addReaction(msg.id, msg.reactions, emoji) }
            },
            onReply = { replyingToMessage = msg },
            onForward = { messageToForward = msg },
            onEdit = {
                messageToEdit = msg
                editTextValue = msg.content
            },
            onCopy = {
                clipboardManager.setText(AnnotatedString(msg.content))
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            },
            onDelete = { forEveryone ->
                scope.launch { repository.deleteMessage(msg.id, forEveryone) }
            }
        )
    }

    // Edit Message Dialog
    messageToEdit?.let { msg ->
        AlertDialog(
            onDismissRequest = { messageToEdit = null },
            title = { Text("Edit Message", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = editTextValue,
                    onValueChange = { editTextValue = it },
                    label = { Text("Message") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val edited = editTextValue.trim()
                        if (edited.isNotBlank()) {
                            scope.launch { repository.editMessage(msg.id, edited) }
                            messageToEdit = null
                        }
                    }
                ) {
                    Text("Save", color = WhatsAppGreenPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { messageToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Forward Message Dialog
    messageToForward?.let { msg ->
        BasicAlertDialog(onDismissRequest = { messageToForward = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Forward message to...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = WhatsAppGreenPrimary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val targets = allChats.filter { it.id != chatId }
                    targets.forEach { targetChat ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    scope.launch {
                                        repository.sendMessage(
                                            chatId = targetChat.id,
                                            content = msg.content,
                                            messageType = msg.messageType,
                                            mediaDurationSec = msg.mediaDurationSec
                                        )
                                    }
                                    messageToForward = null
                                    Toast.makeText(context, "Forwarded to ${targetChat.name}", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 10.dp, horizontal = 6.dp)
                        ) {
                            WhatsAppAvatar(name = targetChat.name, isGroup = targetChat.isGroup, isAiChat = targetChat.isAiChat, size = 36.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = targetChat.name, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = { messageToForward = null },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }

    // AI Chat Summary Dialog
    if (showAiSummaryDialog) {
        AlertDialog(
            onDismissRequest = { showAiSummaryDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI",
                        tint = MetaAiBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("NexAI Conversation Summary", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                if (isGeneratingSummary) {
                    Text("Analyzing conversation thread and key takeaways...")
                } else {
                    Text(text = aiSummaryContent, fontSize = 13.5.sp, lineHeight = 19.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showAiSummaryDialog = false }) {
                    Text("Done", color = WhatsAppGreenPrimary)
                }
            }
        )
    }

    // End-to-End Encryption Verification Dialog
    if (showEncryptionDialog) {
        AlertDialog(
            onDismissRequest = { showEncryptionDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = WhatsAppGreenPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Verify Security Code", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "To verify that end-to-end encryption is active with ${currentChat.name}, scan the QR code or compare the 60-digit number.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(120.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "Security QR Code",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "83921  49201  38491  02948\n58392  10482  94820  10492\n48192  03948  28491  94820",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showEncryptionDialog = false }) {
                    Text("Verified", color = WhatsAppGreenPrimary)
                }
            }
        )
    }

    // Delete Contact & Chat Confirmation Dialog
    if (showDeleteContactDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteContactDialog = false },
            title = {
                Text(
                    text = "Delete Contact / Chat?",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${currentChat.name}\"? This will remove the conversation and contact from your app.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            repository.deleteChat(chatId)
                            showDeleteContactDialog = false
                            Toast.makeText(context, "${currentChat.name} deleted", Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteContactDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
