package com.example.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.TickBlue
import com.example.ui.theme.TickGray

@Composable
fun WhatsAppTicks(
    status: String,
    size: Dp = 15.dp,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        when (status.uppercase()) {
            "PENDING" -> {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "Pending",
                    tint = TickGray,
                    modifier = Modifier.size(size)
                )
            }
            "SENT" -> {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Sent",
                    tint = TickGray,
                    modifier = Modifier.size(size)
                )
            }
            "DELIVERED" -> {
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = "Delivered",
                    tint = TickGray,
                    modifier = Modifier.size(size)
                )
            }
            "READ" -> {
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = "Read",
                    tint = TickBlue,
                    modifier = Modifier.size(size)
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = "Status",
                    tint = TickGray,
                    modifier = Modifier.size(size)
                )
            }
        }
    }
}
