package com.example.hobbyhive.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hobbyhive.data.UserPreferencesRepository
import com.example.hobbyhive.ui.components.HobbyButton
import com.example.hobbyhive.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingPage(val emoji: String, val title: String, val description: String)

@Composable
fun OnboardingScreen(
    userPreferencesRepository: UserPreferencesRepository,
    onFinish: () -> Unit
) {
    val pages = listOf(
        OnboardingPage("🐝", "Welcome to HobbyHive", "Discover, track, and grow your hobbies — all in one beautiful app."),
        OnboardingPage("📊", "Track Your Progress", "Set goals, rate your skills, and watch your progress grow with visual indicators."),
        OnboardingPage("🔔", "Stay on Track", "Set reminders so you never miss a practice session. Build streaks and stay motivated!"),
        OnboardingPage("🎨", "Express Yourself", "Add photos, notes, and categories. Your hobby journey, beautifully organized.")
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        DotGridBackground()

        Column(Modifier.fillMaxSize().padding(24.dp)) {
            Spacer(Modifier.height(48.dp))

            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    if (page == 0) {
                        BeeMascot(size = 120.dp)
                    } else {
                        Text(pages[page].emoji, fontSize = 80.sp)
                    }
                    Spacer(Modifier.height(32.dp))
                    Text(
                        pages[page].title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        color = InkBlack,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        pages[page].description,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        color = Charcoal,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        lineHeight = 22.sp
                    )
                }
            }

            // Page indicators — sticker dots
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                repeat(pages.size) { index ->
                    val isActive = pagerState.currentPage == index
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isActive) 24.dp else 10.dp, 10.dp),
                        shape = RoundedCornerShape(50),
                        color = if (isActive) HoneyYellow else PaperWarm,
                        border = BorderStroke(1.5.dp, InkBlack)
                    ) {}
                }
            }

            Spacer(Modifier.height(32.dp))

            if (pagerState.currentPage == pages.size - 1) {
                HobbyButton(text = "Get Started 🐝", onClick = {
                    scope.launch {
                        userPreferencesRepository.setOnboardingCompleted()
                        onFinish()
                    }
                })
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = {
                        scope.launch {
                            userPreferencesRepository.setOnboardingCompleted()
                            onFinish()
                        }
                    }) { Text("Skip", color = Charcoal, fontWeight = FontWeight.Bold) }
                    HobbyButton(
                        text = "Next →",
                        onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                        modifier = Modifier.width(120.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
