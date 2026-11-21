package com.example.ui.screens.status

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BasicAlertDialog
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.WhatsAppAvatar
import com.example.ui.theme.WhatsAppGreenAccent
import com.example.ui.theme.WhatsAppGreenPrimary

data class StatusStory(
    val contactName: String,
    val timeAgo: String,
    val textContent: String,
    val bgColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusUpdatesScreen(
    modifier: Modifier = Modifier
) {
    var myStatusText by remember { mutableStateOf("Enjoying a productive day with NexChat! 🚀") }
    var myStatusTime by remember { mutableStateOf("Today, 10:15 AM") }

    val contactStories = remember {
        mutableStateListOf(
            StatusStory("Sarah Jenkins", "25 minutes ago", "Finished user research phase! Amazing insights coming up ✨", Color(0xFF008069)),
            StatusStory("David Chen", "2 hours ago", "Coffee + Kotlin Compose = Flow state ☕💻", Color(0xFF1E88E5)),
            StatusStory("Emma Watson", "4 hours ago", "Weekend road trip countdown starts now 🚗🌲", Color(0xFFE53935))
        )
    }

    var viewingStory by remember { mutableStateOf<StatusStory?>(null) }
    var showAddStatusDialog by remember { mutableStateOf(false) }
    var newStatusInput by remember { mutableStateOf("") }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Updates", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                // My Status item
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewingStory = StatusStory("My Status", myStatusTime, myStatusText, WhatsAppGreenPrimary)
                            }
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            WhatsAppAvatar(name = "Alex Morgan", size = 52.dp)
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(WhatsAppGreenAccent)
                                    .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Status",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "My Status", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = myStatusTime, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Text(
                        text = "Recent updates",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Contacts updates
                items(contactStories.size) { index ->
                    val story = contactStories[index]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewingStory = story }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        // Green status ring around avatar
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .border(2.5.dp, WhatsAppGreenAccent, CircleShape)
                                .padding(3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            WhatsAppAvatar(name = story.contactName, size = 48.dp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(text = story.contactName, fontWeight = FontWeight.Bold, fontSize = 15.5.sp)
                            Text(text = story.timeAgo, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // FAB to add new text status
        FloatingActionButton(
            onClick = { showAddStatusDialog = true },
            containerColor = WhatsAppGreenPrimary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp)
        ) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Add Status")
        }
    }

    // Status Story Viewer Dialog
    viewingStory?.let { story ->
        BasicAlertDialog(onDismissRequest = { viewingStory = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = story.bgColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp)
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Story Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        WhatsAppAvatar(name = story.contactName, size = 40.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = story.contactName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(text = story.timeAgo, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        }
                        IconButton(onClick = { viewingStory = null }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = story.textContent,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 32.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }

    // Add Status Dialog
    if (showAddStatusDialog) {
        BasicAlertDialog(onDismissRequest = { showAddStatusDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("New Status Update", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = WhatsAppGreenPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newStatusInput,
                        onValueChange = { newStatusInput = it },
                        placeholder = { Text("What's on your mind?") },
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.align(Alignment.End)) {
                        TextButton(onClick = { showAddStatusDialog = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                if (newStatusInput.isNotBlank()) {
                                    myStatusText = newStatusInput.trim()
                                    myStatusTime = "Just now"
                                    newStatusInput = ""
                                    showAddStatusDialog = false
                                }
                            }
                        ) {
                            Text("Post Status", color = WhatsAppGreenPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
