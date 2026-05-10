package com.example.hobbyhive.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hobbyhive.model.Hobby
import com.example.hobbyhive.ui.theme.*

// ═══════════════════════════════════════════════════
// HobbyCard — Sticker-style hobby list card
// Thick border, playful layout, warm colors
// ═══════════════════════════════════════════════════

@Composable
fun HobbyCard(
    hobby: Hobby,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PaperWhite
        ),
        border = BorderStroke(2.5.dp, InkBlack),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail — emoji or image
            Card(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = HoneyLight
                ),
                border = BorderStroke(2.dp, InkBlack)
            ) {
                if (hobby.imageUri != null) {
                    AsyncImage(
                        model = hobby.imageUri,
                        contentDescription = hobby.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = hobby.category.emoji,
                            fontSize = 28.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = hobby.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = InkBlack
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Category sticker chip
                Surface(
                    shape = RoundedCornerShape(50),
                    color = LimeCardBg,
                    border = BorderStroke(1.5.dp, InkBlack)
                ) {
                    Text(
                        text = "${hobby.category.emoji} ${hobby.category.displayName}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkBlack,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress bar with chunky style
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GradientProgressBar(
                        progress = hobby.progress / 100f,
                        colors = listOf(HoneyYellow, HoneyGold),
                        modifier = Modifier.weight(1f),
                        height = 8.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${hobby.progress}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = InkBlack
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Mini rating
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RatingBar(
                    rating = hobby.rating,
                    starSize = 16.dp,
                    maxStars = 5,
                    activeColor = HoneyYellow
                )
                Text(
                    text = String.format("%.1f", hobby.rating),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = InkBlack,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
