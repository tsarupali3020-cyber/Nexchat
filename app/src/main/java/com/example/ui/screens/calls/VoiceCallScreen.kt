package com.example.ui.screens.calls

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.CallState
import com.example.service.VoiceCallManager
import com.example.ui.components.WhatsAppAvatar
import com.example.ui.theme.WhatsAppGreenAccent

@Composable
fun VoiceCallScreen(
    callManager: VoiceCallManager,
    modifier: Modifier = Modifier
) {
    val callInfo by callManager.callState.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_ring")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF101D24))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 32.dp, horizontal = 24.dp)
        ) {
            // Security badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "End-to-End Encrypted",
                    tint = Color(0xFF8696A0),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "End-to-end encrypted voice call",
                    color = Color(0xFF8696A0),
                    fontSize = 12.sp
                )
            }

            Text(
                text = callInfo.contactName.ifBlank { "Voice Call" },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            val statusText = when (callInfo.state) {
                CallState.OUTGOING_RINGING -> "Ringing..."
                CallState.INCOMING_RINGING -> "Incoming voice call..."
                CallState.CONNECTED -> {
                    val mins = callInfo.durationSeconds / 60
                    val secs = callInfo.durationSeconds % 60
                    "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
                }
                CallState.ENDED -> "Call ended"
                else -> ""
            }

            Text(
                text = statusText,
                fontSize = 15.sp,
                color = if (callInfo.state == CallState.CONNECTED) WhatsAppGreenAccent else Color(0xFF8696A0)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Pulsing Avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(160.dp)
            ) {
                if (callInfo.state == CallState.OUTGOING_RINGING || callInfo.state == CallState.INCOMING_RINGING) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(Color(0xFF00A884).copy(alpha = 0.2f))
                    )
                }

                WhatsAppAvatar(
                    name = callInfo.contactName,
                    size = 110.dp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Controls Bar
            if (callInfo.state == CallState.INCOMING_RINGING) {
                // Incoming Call: Reject and Accept Buttons
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { callManager.rejectIncomingCall() },
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE53935))
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallEnd,
                                contentDescription = "Decline Call",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Decline", color = Color.White, fontSize = 13.sp)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { callManager.acceptIncomingCall() },
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00A884))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Accept Call",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Accept", color = Color.White, fontSize = 13.sp)
                    }
                }
            } else {
                // Active / Outgoing Call Controls
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    // Speakerphone Toggle
                    IconButton(
                        onClick = { callManager.toggleSpeaker() },
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (callInfo.isSpeakerOn) Color.White else Color(0xFF202C33))
                    ) {
                        Icon(
                            imageVector = if (callInfo.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                            contentDescription = "Speaker",
                            tint = if (callInfo.isSpeakerOn) Color.Black else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Mute Microphone Toggle
                    IconButton(
                        onClick = { callManager.toggleMute() },
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (callInfo.isMuted) Color.White else Color(0xFF202C33))
                    ) {
                        Icon(
                            imageVector = if (callInfo.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = if (callInfo.isMuted) Color.Black else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // End Call
                    IconButton(
                        onClick = { callManager.endCall() },
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE53935))
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}
