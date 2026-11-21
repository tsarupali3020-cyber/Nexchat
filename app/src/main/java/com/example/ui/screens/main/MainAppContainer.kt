package com.example.ui.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AiAssistantService
import com.example.data.repository.ChatRepository
import com.example.service.CallState
import com.example.service.VoiceCallManager
import com.example.ui.screens.ai.AiAssistantScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.auth.SecurityLockdownScreen
import com.example.ui.screens.calls.CallsScreen
import com.example.ui.screens.calls.VoiceCallScreen
import com.example.ui.screens.chat.ChatsListScreen
import com.example.ui.screens.chat.ConversationScreen
import com.example.ui.screens.contacts.ContactsScreen
import com.example.ui.screens.settings.LinkedDevicesScreen
import com.example.ui.screens.settings.PrivacyScreen
import com.example.ui.screens.settings.SecurityScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.status.StatusUpdatesScreen
import com.example.ui.theme.MetaAiBlue
import com.example.ui.theme.WhatsAppGreenAccent
import com.example.ui.theme.WhatsAppGreenPrimary

enum class AppTab {
    CHATS,
    UPDATES,
    CALLS,
    SETTINGS
}

enum class SubScreen {
    NONE,
    CONVERSATION,
    AI_ASSISTANT,
    LINKED_DEVICES,
    SECURITY,
    PRIVACY,
    CONTACTS
}

@Composable
fun MainAppContainer(
    repository: ChatRepository,
    aiService: AiAssistantService,
    callManager: VoiceCallManager,
    modifier: Modifier = Modifier
) {
    val account by repository.userAccount.collectAsState(initial = null)
    val callInfo by callManager.callState.collectAsState()
    val allChats by repository.allChats.collectAsState(initial = emptyList())
    val totalUnread = allChats.sumOf { it.unreadCount }

    var selectedTab by remember { mutableStateOf(AppTab.CHATS) }
    var currentSubScreen by remember { mutableStateOf(SubScreen.NONE) }
    var selectedChatId by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // 1. Security Lockdown Screen Check
    if (account?.isSecurityLocked == true) {
        SecurityLockdownScreen(
            reason = account?.securityLockReason.orEmpty(),
            repository = repository
        )
        return
    }

    // 2. Active Voice Call Fullscreen Overlay Check
    if (callInfo.state != CallState.IDLE) {
        VoiceCallScreen(callManager = callManager)
        return
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        if (isWideScreen) {
            // Adaptive Tablet / Desktop Two-Pane Layout (WhatsApp Web Style)
            Row(modifier = Modifier.fillMaxSize()) {
                // Navigation Rail
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxHeight().statusBarsPadding().navigationBarsPadding()
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    NavigationRailItem(
                        selected = selectedTab == AppTab.CHATS && currentSubScreen == SubScreen.NONE,
                        onClick = {
                            selectedTab = AppTab.CHATS
                            currentSubScreen = SubScreen.NONE
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (totalUnread > 0) {
                                        Badge(containerColor = WhatsAppGreenAccent) {
                                            Text(totalUnread.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = "Chats")
                            }
                        },
                        label = { Text("Chats") },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = WhatsAppGreenPrimary,
                            indicatorColor = WhatsAppGreenPrimary.copy(alpha = 0.15f)
                        )
                    )

                    NavigationRailItem(
                        selected = selectedTab == AppTab.UPDATES && currentSubScreen == SubScreen.NONE,
                        onClick = {
                            selectedTab = AppTab.UPDATES
                            currentSubScreen = SubScreen.NONE
                        },
                        icon = { Icon(Icons.Default.RadioButtonChecked, contentDescription = "Updates") },
                        label = { Text("Updates") },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = WhatsAppGreenPrimary,
                            indicatorColor = WhatsAppGreenPrimary.copy(alpha = 0.15f)
                        )
                    )

                    NavigationRailItem(
                        selected = selectedTab == AppTab.CALLS && currentSubScreen == SubScreen.NONE,
                        onClick = {
                            selectedTab = AppTab.CALLS
                            currentSubScreen = SubScreen.NONE
                        },
                        icon = { Icon(Icons.Default.Call, contentDescription = "Calls") },
                        label = { Text("Calls") },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = WhatsAppGreenPrimary,
                            indicatorColor = WhatsAppGreenPrimary.copy(alpha = 0.15f)
                        )
                    )

                    NavigationRailItem(
                        selected = currentSubScreen == SubScreen.AI_ASSISTANT,
                        onClick = { currentSubScreen = SubScreen.AI_ASSISTANT },
                        icon = {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = "NexAI",
                                tint = MetaAiBlue
                            )
                        },
                        label = { Text("NexAI") },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = MetaAiBlue,
                            indicatorColor = MetaAiBlue.copy(alpha = 0.15f)
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    NavigationRailItem(
                        selected = selectedTab == AppTab.SETTINGS && currentSubScreen == SubScreen.NONE,
                        onClick = {
                            selectedTab = AppTab.SETTINGS
                            currentSubScreen = SubScreen.NONE
                        },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = WhatsAppGreenPrimary,
                            indicatorColor = WhatsAppGreenPrimary.copy(alpha = 0.15f)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Left Pane: List View
                Box(modifier = Modifier.width(360.dp).fillMaxHeight()) {
                    when (selectedTab) {
                        AppTab.CHATS -> {
                            ChatsListScreen(
                                repository = repository,
                                callManager = callManager,
                                onChatSelected = { chatId ->
                                    selectedChatId = chatId
                                    currentSubScreen = SubScreen.CONVERSATION
                                },
                                onNavigateToAi = { currentSubScreen = SubScreen.AI_ASSISTANT },
                                onNavigateToSettings = {
                                    selectedTab = AppTab.SETTINGS
                                    currentSubScreen = SubScreen.NONE
                                },
                                onNavigateToContacts = { currentSubScreen = SubScreen.CONTACTS }
                            )
                        }
                        AppTab.UPDATES -> StatusUpdatesScreen()
                        AppTab.CALLS -> CallsScreen(repository = repository, callManager = callManager)
                        AppTab.SETTINGS -> {
                            SettingsScreen(
                                repository = repository,
                                onNavigateToLinkedDevices = { currentSubScreen = SubScreen.LINKED_DEVICES },
                                onNavigateToSecurity = { currentSubScreen = SubScreen.SECURITY },
                                onNavigateToPrivacy = { currentSubScreen = SubScreen.PRIVACY },
                                onNavigateToAi = { currentSubScreen = SubScreen.AI_ASSISTANT }
                            )
                        }
                    }
                }

                // Right Pane: Detail View
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when (currentSubScreen) {
                        SubScreen.CONVERSATION -> {
                            val chatId = selectedChatId ?: "ai_assistant_chat"
                            ConversationScreen(
                                chatId = chatId,
                                repository = repository,
                                aiService = aiService,
                                callManager = callManager,
                                onBack = { currentSubScreen = SubScreen.NONE }
                            )
                        }
                        SubScreen.AI_ASSISTANT -> {
                            AiAssistantScreen(
                                repository = repository,
                                aiService = aiService,
                                onBack = { currentSubScreen = SubScreen.NONE }
                            )
                        }
                        SubScreen.LINKED_DEVICES -> {
                            LinkedDevicesScreen(
                                repository = repository,
                                onBack = { currentSubScreen = SubScreen.NONE }
                            )
                        }
                        SubScreen.SECURITY -> {
                            SecurityScreen(
                                repository = repository,
                                onBack = { currentSubScreen = SubScreen.NONE }
                            )
                        }
                        SubScreen.PRIVACY -> {
                            PrivacyScreen(
                                repository = repository,
                                onBack = { currentSubScreen = SubScreen.NONE }
                            )
                        }
                        SubScreen.CONTACTS -> {
                            ContactsScreen(
                                repository = repository,
                                callManager = callManager,
                                onSelectContactForChat = { name, phone ->
                                    scope.launch {
                                        val chatId = repository.createNewChat(name, phone, false)
                                        selectedChatId = chatId
                                        currentSubScreen = SubScreen.CONVERSATION
                                    }
                                },
                                onBack = { currentSubScreen = SubScreen.NONE }
                            )
                        }
                        SubScreen.NONE -> {
                            // Desktop Placeholder Screen
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(32.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = WhatsAppGreenPrimary.copy(alpha = 0.12f),
                                        modifier = Modifier.size(100.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Chat,
                                                contentDescription = "Chat",
                                                tint = WhatsAppGreenPrimary,
                                                modifier = Modifier.size(50.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text(
                                        text = "NexChat for Tablet & Desktop",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Send and receive messages without keeping your phone online.\nSelect a chat from the left panel to begin.",
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Encrypted",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "End-to-end encrypted",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Compact Phone Navigation Layout
            when (currentSubScreen) {
                SubScreen.CONVERSATION -> {
                    val chatId = selectedChatId ?: "ai_assistant_chat"
                    ConversationScreen(
                        chatId = chatId,
                        repository = repository,
                        aiService = aiService,
                        callManager = callManager,
                        onBack = { currentSubScreen = SubScreen.NONE }
                    )
                }
                SubScreen.AI_ASSISTANT -> {
                    AiAssistantScreen(
                        repository = repository,
                        aiService = aiService,
                        onBack = { currentSubScreen = SubScreen.NONE }
                    )
                }
                SubScreen.LINKED_DEVICES -> {
                    LinkedDevicesScreen(
                        repository = repository,
                        onBack = { currentSubScreen = SubScreen.NONE }
                    )
                }
                SubScreen.SECURITY -> {
                    SecurityScreen(
                        repository = repository,
                        onBack = { currentSubScreen = SubScreen.NONE }
                    )
                }
                SubScreen.PRIVACY -> {
                    PrivacyScreen(
                        repository = repository,
                        onBack = { currentSubScreen = SubScreen.NONE }
                    )
                }
                SubScreen.CONTACTS -> {
                    ContactsScreen(
                        repository = repository,
                        callManager = callManager,
                        onSelectContactForChat = { name, phone ->
                            scope.launch {
                                val chatId = repository.createNewChat(name, phone, false)
                                selectedChatId = chatId
                                currentSubScreen = SubScreen.CONVERSATION
                            }
                        },
                        onBack = { currentSubScreen = SubScreen.NONE }
                    )
                }
                SubScreen.NONE -> {
                    Scaffold(
                        bottomBar = {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.navigationBarsPadding()
                            ) {
                                NavigationBarItem(
                                    selected = selectedTab == AppTab.CHATS,
                                    onClick = { selectedTab = AppTab.CHATS },
                                    icon = {
                                        BadgedBox(
                                            badge = {
                                                if (totalUnread > 0) {
                                                    Badge(containerColor = WhatsAppGreenAccent) {
                                                        Text(totalUnread.toString())
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(Icons.Default.Chat, contentDescription = "Chats")
                                        }
                                    },
                                    label = { Text("Chats") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = WhatsAppGreenPrimary,
                                        indicatorColor = WhatsAppGreenPrimary.copy(alpha = 0.15f)
                                    )
                                )

                                NavigationBarItem(
                                    selected = selectedTab == AppTab.UPDATES,
                                    onClick = { selectedTab = AppTab.UPDATES },
                                    icon = { Icon(Icons.Default.RadioButtonChecked, contentDescription = "Updates") },
                                    label = { Text("Updates") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = WhatsAppGreenPrimary,
                                        indicatorColor = WhatsAppGreenPrimary.copy(alpha = 0.15f)
                                    )
                                )

                                NavigationBarItem(
                                    selected = selectedTab == AppTab.CALLS,
                                    onClick = { selectedTab = AppTab.CALLS },
                                    icon = { Icon(Icons.Default.Call, contentDescription = "Calls") },
                                    label = { Text("Calls") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = WhatsAppGreenPrimary,
                                        indicatorColor = WhatsAppGreenPrimary.copy(alpha = 0.15f)
                                    )
                                )

                                NavigationBarItem(
                                    selected = selectedTab == AppTab.SETTINGS,
                                    onClick = { selectedTab = AppTab.SETTINGS },
                                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                    label = { Text("Settings") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = WhatsAppGreenPrimary,
                                        indicatorColor = WhatsAppGreenPrimary.copy(alpha = 0.15f)
                                    )
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            when (selectedTab) {
                                AppTab.CHATS -> {
                                    ChatsListScreen(
                                        repository = repository,
                                        callManager = callManager,
                                        onChatSelected = { chatId ->
                                            selectedChatId = chatId
                                            currentSubScreen = SubScreen.CONVERSATION
                                        },
                                        onNavigateToAi = { currentSubScreen = SubScreen.AI_ASSISTANT },
                                        onNavigateToSettings = { selectedTab = AppTab.SETTINGS },
                                        onNavigateToContacts = { currentSubScreen = SubScreen.CONTACTS }
                                    )
                                }
                                AppTab.UPDATES -> StatusUpdatesScreen()
                                AppTab.CALLS -> CallsScreen(repository = repository, callManager = callManager)
                                AppTab.SETTINGS -> {
                                    SettingsScreen(
                                        repository = repository,
                                        onNavigateToLinkedDevices = { currentSubScreen = SubScreen.LINKED_DEVICES },
                                        onNavigateToSecurity = { currentSubScreen = SubScreen.SECURITY },
                                        onNavigateToPrivacy = { currentSubScreen = SubScreen.PRIVACY },
                                        onNavigateToAi = { currentSubScreen = SubScreen.AI_ASSISTANT }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
