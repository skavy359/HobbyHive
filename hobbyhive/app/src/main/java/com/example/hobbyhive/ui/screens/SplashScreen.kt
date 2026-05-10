package com.example.hobbyhive.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hobbyhive.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

import com.example.hobbyhive.data.UserPreferencesRepository
import kotlinx.coroutines.flow.first

@Composable
fun SplashScreen(
    userPreferencesRepository: UserPreferencesRepository,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    // Timing: 1.5 seconds total
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(1500)
        
        val onboardingCompleted = userPreferencesRepository.isOnboardingCompleted.first()
        val isLoggedIn = userPreferencesRepository.isLoggedIn.first()
        
        if (!onboardingCompleted) {
            onNavigateToOnboarding()
        } else if (isLoggedIn) {
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }

    // Animations
    val beeScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label = "bee_scale"
    )

    val beeAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(400),
        label = "bee_alpha"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(600, delayMillis = 300),
        label = "text_alpha"
    )

    val taglineAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(500, delayMillis = 600),
        label = "tagline_alpha"
    )

    // Floating honeycomb animation
    val infiniteTransition = rememberInfiniteTransition(label = "honeycomb")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HoneyYellow),
        contentAlignment = Alignment.Center
    ) {
        // Background honeycomb pattern
        Canvas(modifier = Modifier.fillMaxSize()) {
            val hexR = 40.dp.toPx()
            val hexW = hexR * 2
            val hexH = hexR * kotlin.math.sqrt(3f)
            val cols = (size.width / (hexW * 0.75f)).toInt() + 2
            val rows = (size.height / hexH).toInt() + 2

            for (col in -1..cols) {
                for (row in -1..rows) {
                    val offsetX = if (row % 2 == 1) hexW * 0.375f else 0f
                    val cx = col * hexW * 0.75f + offsetX
                    val cy = row * hexH * 0.5f

                    drawHexOutline(
                        Offset(cx, cy),
                        hexR * 0.9f,
                        InkBlack.copy(alpha = 0.06f),
                        2.dp.toPx()
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Bee mascot
            Box(
                modifier = Modifier
                    .scale(beeScale)
                    .alpha(beeAlpha)
                    .offset(y = floatOffset.dp)
            ) {
                BeeMascot(size = 120.dp)
            }

            Spacer(Modifier.height(24.dp))

            // App name
            Text(
                text = "HobbyHive",
                modifier = Modifier.alpha(textAlpha),
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = InkBlack,
                letterSpacing = (-1).sp
            )

            Spacer(Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Find Your Passion ✿ Track Your Progress",
                modifier = Modifier.alpha(taglineAlpha),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = InkBlack.copy(alpha = 0.6f),
                letterSpacing = 0.5.sp
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHexOutline(
    center: Offset,
    radius: Float,
    color: Color,
    strokeWidth: Float
) {
    val path = Path()
    for (i in 0 until 6) {
        val angle = Math.toRadians((i * 60 - 30).toDouble())
        val px = center.x + radius * cos(angle).toFloat()
        val py = center.y + radius * sin(angle).toFloat()
        if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
    }
    path.close()
    drawPath(path, color, style = Stroke(width = strokeWidth))
}
