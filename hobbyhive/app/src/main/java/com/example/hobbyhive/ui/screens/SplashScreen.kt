package com.example.hobbyhive.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.hobbyhive.R
import com.example.hobbyhive.data.UserPreferencesRepository
import com.example.hobbyhive.ui.components.HobbyText
import com.example.hobbyhive.ui.theme.AccentAmber
import com.example.hobbyhive.ui.theme.DotGridBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

// ═══════════════════════════════════════════════════
// SplashScreen — App entry with animated branding
// ═══════════════════════════════════════════════════

@Composable
fun SplashScreen(
    userPreferencesRepository: UserPreferencesRepository,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    // Animation states
    val logoScale = remember { Animatable(0.5f) }
    val contentAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Animate in
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        contentAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(800)
        )

        // Wait for splash display
        delay(2500L)

        // Check navigation destination
        val onboardingCompleted = userPreferencesRepository.isOnboardingCompleted.first()
        if (!onboardingCompleted) {
            onNavigateToOnboarding()
            return@LaunchedEffect
        }

        val isLoggedIn = userPreferencesRepository.isLoggedIn.first()
        if (isLoggedIn) {
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Dot grid background
        DotGridBackground()

        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "HobbyHive Logo",
                modifier = Modifier
                    .scale(logoScale.value)
                    .size(100.dp)
                    .clip(RoundedCornerShape(24.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // App name with gradient
            HobbyText(
                text = "HobbyHive",
                useGradient = true,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.alpha(contentAlpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Find Your Passion. Track Your Progress.",
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.alpha(contentAlpha.value)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Loading indicator
            CircularProgressIndicator(
                color = AccentAmber,
                strokeWidth = 3.dp,
                modifier = Modifier
                    .size(40.dp)
                    .alpha(contentAlpha.value)
            )

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
