package com.example.ui.screens.ai

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.repository.AiAssistantService
import com.example.data.repository.ChatRepository
import com.example.ui.components.MessageBubble
import com.example.ui.components.WhatsAppAvatar
import com.example.ui.theme.DarkChatBackground
import com.example.ui.theme.DarkInputBackground
import com.example.ui.theme.LightChatBackground
import com.example.ui.theme.MetaAiBlue
import com.example.ui.theme.WhatsAppGreenPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    repository: ChatRepository,
    aiService: AiAssistantService,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()

    val messages by repository.getMessages("ai_assistant_chat").collectAsState(initial = emptyList())

    var inputPrompt by remember { mutableStateOf("") }
    var showRewriteDialog by remember { mutableStateOf(false) }
    var rewriteDraftText by remember { mutableStateOf("") }
    var rewriteStyle by remember { mutableStateOf("Professional") }
    var rewrittenResult by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickChips = listOf(
        "🧠 Explain Quantum Computing",
        "✍️ Rewrite my message",
        "💡 3 Creative Project Ideas",
        "🌐 Translate to Spanish",
        "🔒 How is encryption handled?"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) DarkChatBackground else LightChatBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WhatsAppAvatar(name = "NexAI", isAiChat = true, size = 38.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "NexAI Assistant",
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "with Meta AI inspiration",
                            fontSize = 11.5.sp,
                            color = MetaAiBlue
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(onClick = { showRewriteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = "Message Polisher",
                        tint = MetaAiBlue
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Notice Banner
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = MetaAiBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Smart assistant ready. Real-time Gemini Cloud integration connects when Firebase is configured.",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Messages list
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = 8.dp),
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            items(messages, key = { it.id }) { message ->
                MessageBubble(
                    message = message,
                    isDark = isDark,
                    onLongPress = {
                        clipboardManager.setText(AnnotatedString(message.content))
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // Quick Suggestion Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(quickChips) { chip ->
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 1.dp,
                    modifier = Modifier.clickable {
                        if (chip.contains("Rewrite")) {
                            showRewriteDialog = true
                        } else {
                            scope.launch {
                                repository.sendMessage(
                                    chatId = "ai_assistant_chat",
                                    content = chip
                                )
                            }
                        }
                    }
                ) {
                    Text(
                        text = chip,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }

        // Composer
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = if (isDark) DarkInputBackground else MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp,
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = inputPrompt,
                    onValueChange = { inputPrompt = it },
                    placeholder = { Text("Ask NexAI anything...", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = {
                    val prompt = inputPrompt.trim()
                    if (prompt.isNotBlank()) {
                        inputPrompt = ""
                        scope.launch {
                            repository.sendMessage(
                                chatId = "ai_assistant_chat",
                                content = prompt
                            )
                        }
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(WhatsAppGreenPrimary)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send to AI",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    // Message Rewrite Tool Modal
    if (showRewriteDialog) {
        BasicAlertDialog(onDismissRequest = { showRewriteDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "✍️ NexAI Message Polisher",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MetaAiBlue
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Enter a draft to polish and make it clear and impactful:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rewriteDraftText,
                        onValueChange = { rewriteDraftText = it },
                        placeholder = { Text("e.g. can we meet tmrw to discuss the presentation") },
                        modifier = Modifier.fillMaxWidth().height(90.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Professional", "Friendly", "Concise").forEach { style ->
                            FilterChip(
                                selected = rewriteStyle == style,
                                onClick = { rewriteStyle = style },
                                label = { Text(style, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(
                        onClick = {
                            if (rewriteDraftText.isNotBlank()) {
                                scope.launch {
                                    rewrittenResult = aiService.rewriteMessage(rewriteDraftText, rewriteStyle)
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Polish Message", color = MetaAiBlue, fontWeight = FontWeight.Bold)
                    }

                    if (rewrittenResult.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "Polished Result:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MetaAiBlue)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = rewrittenResult, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                    TextButton(onClick = {
                                        clipboardManager.setText(AnnotatedString(rewrittenResult))
                                        Toast.makeText(context, "Copied polished message!", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Text("Copy")
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(
                        onClick = {
                            showRewriteDialog = false
                            rewrittenResult = ""
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}
