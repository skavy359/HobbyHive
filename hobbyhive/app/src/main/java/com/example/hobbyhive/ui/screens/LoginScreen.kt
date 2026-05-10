package com.example.hobbyhive.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import com.example.hobbyhive.data.UserPreferencesRepository
import com.example.hobbyhive.data.UserRepository
import com.example.hobbyhive.ui.components.HobbyButton
import com.example.hobbyhive.ui.components.HobbyText
import com.example.hobbyhive.ui.components.HobbyTextField
import com.example.hobbyhive.ui.theme.*
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
        val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        if (!emailRegex.matches(email.trim())) {
            emailError = "Enter a valid email"
            return false
        }
        if (password.length < 8) {
            passwordError = "Password must be at least 8 characters"
            return false
        }
        return true
    }

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        DotGridBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))

            // Bee mascot instead of logo
            BeeMascot(size = 90.dp)

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Welcome Back",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = InkBlack,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Sign in to continue your hobby journey",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Charcoal
            )
            Spacer(Modifier.height(36.dp))

            // Card with sticker style
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(2.5.dp, InkBlack),
                colors = CardDefaults.cardColors(containerColor = PaperWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(Modifier.padding(22.dp)) {
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
                                    contentDescription = null,
                                    tint = InkBlack
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
                            colors = CheckboxDefaults.colors(
                                checkedColor = HoneyYellow,
                                checkmarkColor = InkBlack
                            )
                        )
                        Text(
                            "Remember Me",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = InkBlack
                        )
                        Spacer(Modifier.weight(1f))
                    }

                    if (generalError != null) {
                        Text(
                            generalError!!,
                            color = ErrorRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
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
                    fontSize = 14.sp,
                    color = Charcoal
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        "Sign Up",
                        fontWeight = FontWeight.ExtraBold,
                        color = HoneyGold,
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}
