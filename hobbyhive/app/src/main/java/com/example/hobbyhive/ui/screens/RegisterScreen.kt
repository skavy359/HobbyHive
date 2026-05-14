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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hobbyhive.data.UserRepository
import com.example.hobbyhive.ui.components.HobbyButton
import com.example.hobbyhive.ui.components.HobbyText
import com.example.hobbyhive.ui.components.HobbyTextField
import com.example.hobbyhive.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    userPreferencesRepository: com.example.hobbyhive.data.UserPreferencesRepository,
    appwriteAuthRepository: com.example.hobbyhive.appwrite.repository.AppwriteAuthRepository,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var tcAccepted by remember { mutableStateOf(false) }
    var showTcDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }
    var tcError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    fun validate(): Boolean {
        nameError = null; emailError = null; passwordError = null
        confirmError = null; tcError = null; generalError = null

        if (fullName.trim().length < 2 || !fullName.trim().all { it.isLetter() || it == ' ' }) {
            nameError = "Enter your full name"; return false
        }
        val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        if (!emailRegex.matches(email.trim())) {
            emailError = "Enter a valid email"; return false
        }
        if (password.length < 8 || !password.any { it.isUpperCase() } || !password.any { it.isDigit() }) {
            passwordError = "Min 8 chars, 1 uppercase, 1 digit"; return false
        }
        if (confirmPassword != password) {
            confirmError = "Passwords do not match"; return false
        }
        if (!tcAccepted) {
            tcError = "Please accept the terms to proceed"; return false
        }
        return true
    }

    // T&C Dialog
    if (showTcDialog) {
        AlertDialog(
            onDismissRequest = { showTcDialog = false },
            title = { Text("Terms & Conditions", fontWeight = FontWeight.Black, color = InkBlack) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()).heightIn(max = 400.dp)
                ) {
                    val sections = listOf(
                        "1. App Usage" to "HobbyHive is designed for personal hobby tracking. You agree to use the app responsibly.",
                        "2. Data Storage" to "All data is stored locally on your device using Room database. No data is transmitted to external servers.",
                        "3. Content" to "You are responsible for the hobby content, notes, and images you add to the app.",
                        "4. Privacy" to "We respect your privacy. No personal data is collected or shared with third parties.",
                        "5. Notifications" to "The app may send local notifications for hobby reminders. You can manage these in Settings.",
                        "6. Updates" to "We may update the app to improve functionality. Continued use constitutes acceptance of changes."
                    )
                    sections.forEach { (title, body) ->
                        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = InkBlack)
                        Spacer(Modifier.height(4.dp))
                        Text(body, fontSize = 13.sp, color = Charcoal)
                        Spacer(Modifier.height(12.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { tcAccepted = true; showTcDialog = false }) {
                    Text("Accept", fontWeight = FontWeight.ExtraBold, color = HoneyGold)
                }
            },
            dismissButton = {
                TextButton(onClick = { tcAccepted = false; showTcDialog = false }) {
                    Text("Decline", color = Charcoal)
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        DotGridBackground()
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            // Bee mascot
            BeeMascot(size = 80.dp)

            Spacer(Modifier.height(8.dp))
            Text(
                "Create Account",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = InkBlack,
                letterSpacing = (-0.5).sp
            )
            Spacer(Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(2.5.dp, InkBlack),
                colors = CardDefaults.cardColors(containerColor = PaperWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(Modifier.padding(22.dp)) {
                    HobbyTextField(value = fullName, onValueChange = { fullName = it; nameError = null }, label = "Full Name", leadingIcon = Icons.Default.Person, errorMessage = nameError)
                    Spacer(Modifier.height(16.dp))
                    HobbyTextField(value = email, onValueChange = { email = it; emailError = null }, label = "Email", leadingIcon = Icons.Default.Email, errorMessage = emailError, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                    Spacer(Modifier.height(16.dp))
                    HobbyTextField(
                        value = password, onValueChange = { password = it; passwordError = null }, label = "Password", leadingIcon = Icons.Default.Lock, errorMessage = passwordError,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, tint = InkBlack) } }
                    )
                    Spacer(Modifier.height(16.dp))
                    HobbyTextField(
                        value = confirmPassword, onValueChange = { confirmPassword = it; confirmError = null }, label = "Confirm Password", leadingIcon = Icons.Default.Lock, errorMessage = confirmError,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = { IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) { Icon(if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, tint = InkBlack) } }
                    )
                    Spacer(Modifier.height(12.dp))

                    // T&C
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = tcAccepted, onCheckedChange = { tcAccepted = it }, colors = CheckboxDefaults.colors(checkedColor = HoneyYellow, checkmarkColor = InkBlack))
                        val annotated = buildAnnotatedString {
                            append("I agree to the ")
                            pushStringAnnotation("tc", "tc")
                            withStyle(SpanStyle(color = HoneyGold, fontWeight = FontWeight.ExtraBold)) { append("Terms & Conditions") }
                            pop()
                        }
                        TextButton(onClick = { showTcDialog = true }) { Text(annotated) }
                    }
                    if (tcError != null) { Text(tcError!!, color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp)) }
                    if (generalError != null) { Spacer(Modifier.height(4.dp)); Text(generalError!!, color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp)) }
                    Spacer(Modifier.height(24.dp))

                    HobbyButton(text = "Sign Up", onClick = {
                        if (!validate()) return@HobbyButton
                        isLoading = true
                        scope.launch {
                            appwriteAuthRepository.signUp(fullName.trim(), email.trim(), password).fold(
                                onSuccess = { appwriteUser ->
                                    scope.launch {
                                        // Save the real Appwrite user ID and name
                                        userPreferencesRepository.saveAppwriteUserId(appwriteUser.id, fullName.trim())
                                        isLoading = false
                                        onRegisterSuccess()
                                    }
                                },
                                onFailure = { 
                                    scope.launch {
                                        // Bypassing failure as requested: navigate even if Appwrite fails
                                        // Still save the local name so dashboard shows it
                                        userPreferencesRepository.saveAppwriteUserId("guest", fullName.trim())
                                        isLoading = false
                                        onRegisterSuccess()
                                    }
                                }
                            )
                        }
                    }, isLoading = isLoading, enabled = tcAccepted)
                }
            }
            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account? ", fontSize = 14.sp, color = Charcoal)
                TextButton(onClick = onNavigateToLogin) { Text("Log In", fontWeight = FontWeight.ExtraBold, color = HoneyGold, fontSize = 14.sp) }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}