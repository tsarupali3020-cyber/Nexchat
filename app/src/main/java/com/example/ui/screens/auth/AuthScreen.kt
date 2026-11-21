package com.example.ui.screens.auth

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ChatRepository
import com.example.ui.theme.WhatsAppGreenAccent
import com.example.ui.theme.WhatsAppGreenPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AuthStep {
    PHONE_INPUT,
    OTP_VERIFY,
    PROFILE_SETUP
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    repository: ChatRepository,
    onAuthComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var step by remember { mutableStateOf(AuthStep.PHONE_INPUT) }
    var selectedCountryCode by remember { mutableStateOf("+1") }
    var phoneNumber by remember { mutableStateOf("5552345678") }
    var otpCode by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("Alex Morgan") }
    var userBio by remember { mutableStateOf("Available | Hey there! I am using NexChat.") }

    var countryDropdownExpanded by remember { mutableStateOf(false) }
    val countries = listOf(
        "+1" to "United States / Canada",
        "+44" to "United Kingdom",
        "+91" to "India",
        "+49" to "Germany",
        "+33" to "France",
        "+61" to "Australia",
        "+81" to "Japan"
    )

    var resendTimer by remember { mutableIntStateOf(30) }
    LaunchedEffect(step) {
        if (step == AuthStep.OTP_VERIFY) {
            resendTimer = 30
            while (resendTimer > 0) {
                delay(1000)
                resendTimer -= 1
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().align(Alignment.Center)
        ) {
            // App Logo
            Surface(
                shape = CircleShape,
                color = WhatsAppGreenPrimary,
                modifier = Modifier.size(76.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "NexChat Logo",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (step) {
                AuthStep.PHONE_INPUT -> {
                    Text(
                        text = "Enter your phone number",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "NexChat will send an SMS verification code to verify your phone number.",
                        fontSize = 13.5.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Country Code Picker
                    ExposedDropdownMenuBox(
                        expanded = countryDropdownExpanded,
                        onExpandedChange = { countryDropdownExpanded = !countryDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = countries.firstOrNull { it.first == selectedCountryCode }?.let { "${it.second} (${it.first})" } ?: selectedCountryCode,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Country") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryDropdownExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = countryDropdownExpanded,
                            onDismissRequest = { countryDropdownExpanded = false }
                        ) {
                            countries.forEach { (code, name) ->
                                DropdownMenuItem(
                                    text = { Text("$name ($code)") },
                                    onClick = {
                                        selectedCountryCode = code
                                        countryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Phone Number Input
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it.filter { char -> char.isDigit() } },
                        label = { Text("Phone Number") },
                        leadingIcon = { Text(selectedCountryCode, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            if (phoneNumber.length >= 7) {
                                step = AuthStep.OTP_VERIFY
                            } else {
                                Toast.makeText(context, "Enter a valid phone number", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreenPrimary),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Next", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                AuthStep.OTP_VERIFY -> {
                    Text(
                        text = "Verifying $selectedCountryCode $phoneNumber",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Waiting to automatically detect an SMS sent to your phone. Enter the 6-digit code:",
                        fontSize = 13.5.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { if (it.length <= 6 && it.all { char -> char.isDigit() }) otpCode = it },
                        placeholder = { Text("6-digit code (e.g. 749201)", textAlign = TextAlign.Center) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick test button
                    TextButton(onClick = { otpCode = "749201" }) {
                        Text("Auto-fill code for test: 749201", color = WhatsAppGreenPrimary, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (otpCode.length == 6) {
                                step = AuthStep.PROFILE_SETUP
                            } else {
                                Toast.makeText(context, "Please enter 6 digits", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreenPrimary),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Verify Code", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (resendTimer > 0) "Resend SMS in 0:${resendTimer.toString().padStart(2, '0')}" else "Resend SMS code",
                        color = if (resendTimer > 0) MaterialTheme.colorScheme.onSurfaceVariant else WhatsAppGreenPrimary,
                        fontSize = 13.sp
                    )
                }

                AuthStep.PROFILE_SETUP -> {
                    Text(
                        text = "Profile Info",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Please provide your name and an optional bio for your contacts.",
                        fontSize = 13.5.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Type your name here") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = userBio,
                        onValueChange = { userBio = it },
                        label = { Text("About / Bio") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            if (username.isNotBlank()) {
                                scope.launch {
                                    repository.updateAccount {
                                        it.copy(
                                            phoneNumber = "$selectedCountryCode $phoneNumber",
                                            countryCode = selectedCountryCode,
                                            username = username.trim(),
                                            statusBio = userBio.trim(),
                                            isLoggedIn = true
                                        )
                                    }
                                    onAuthComplete()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreenPrimary),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Finish Setup", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
