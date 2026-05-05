package com.example.hobbyhive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.hobbyhive.data.UserPreferencesRepository
import com.example.hobbyhive.data.UserRepository
import com.example.hobbyhive.ui.components.HobbyButton
import com.example.hobbyhive.ui.components.HobbyText
import com.example.hobbyhive.ui.components.HobbyTextField
import com.example.hobbyhive.ui.theme.DotGridBackground
import com.example.hobbyhive.ui.theme.ElevatedCardShape
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun LoginScreen(
    userRepository: UserRepository,
    userPreferencesRepository: UserPreferencesRepository,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    fun validate(): Boolean {
        emailError = null
        passwordError = null
        generalError = null

        if (email.isBlank()) {
            emailError = "Email cannot be empty"
            return false
        }
        if (password.isBlank()) {
            passwordError = "Password cannot be empty"
            return false
        }
        return true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        DotGridBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))
            Text("🐝", style = MaterialTheme.typography.displayLarge)
            Spacer(Modifier.height(16.dp))
            HobbyText(
                text = "Welcome Back",
                useGradient = true,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            HobbyText(
                text = "Sign in to continue your hobby journey",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = ElevatedCardShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(24.dp)) {
                    HobbyTextField(
                        value = email,
                        onValueChange = { email = it; emailError = null },
                        label = "Email",
                        leadingIcon = Icons.Default.Email,
                        errorMessage = emailError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Spacer(Modifier.height(16.dp))
                    HobbyTextField(
                        value = password,
                        onValueChange = { password = it; passwordError = null },
                        label = "Password",
                        leadingIcon = Icons.Default.Lock,
                        errorMessage = passwordError,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                    Spacer(Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            "Remember Me",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = { /* Handle Forgot Password */ }) {
                            Text("Forgot Password?", color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    if (generalError != null) {
                        Text(
                            generalError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    Spacer(Modifier.height(24.dp))

                    HobbyButton(
                        text = "Log In",
                        onClick = {
                            if (!validate()) return@HobbyButton
                            isLoading = true
                            scope.launch {
                                userRepository.login(email.trim(), password).fold(
                                    onSuccess = { user ->
                                        // Save session
                                        userPreferencesRepository.saveAuthToken(
                                            token = UUID.randomUUID().toString(),
                                            userId = user.id,
                                            keepLoggedIn = rememberMe
                                        )
                                        isLoading = false
                                        onLoginSuccess()
                                    },
                                    onFailure = {
                                        generalError = it.message
                                        isLoading = false
                                    }
                                )
                            }
                        },
                        isLoading = isLoading
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Don't have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        "Sign Up",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}
