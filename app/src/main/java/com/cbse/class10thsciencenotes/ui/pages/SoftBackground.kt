package com.cbse.class10thsciencenotes.ui.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Custom Aesthetic Colors ---
val SoftBackground = Color(0xFFF5F7FA)
val CardSurface = Color(0xFFFFFFFF)
val PrimaryAccent = Color(0xFF6C63FF) // Modern Purple/Blue
val SecondaryAccent = Color(0xFF2D3436) // Dark Slate
val TextGray = Color(0xFF636E72)
val QuestionLabelColor = Color(0xFFFF7675) // Soft Coral

// --- Data Model ---
data class QAItem(
    val question: String,
    val answer: String
)

// Sample Class 10 Data (Real Numbers)
val chapter1Questions = listOf(
    QAItem(
        "Use Euclid's division algorithm to find the HCF of 135 and 225.",
        "Since 225 > 135, we apply the division lemma to 225 and 135 to obtain:\n225 = 135 × 1 + 90\n\nSince remainder 90 ≠ 0, we apply the division lemma to 135 and 90 to obtain:\n135 = 90 × 1 + 45\n\nWe consider the new divisor 90 and new remainder 45, and apply the division lemma to obtain:\n90 = 2 × 45 + 0\n\nSince the remainder is zero, the process stops. The divisor at this stage is 45.\nTherefore, the HCF of 135 and 225 is 45."
    ),
    QAItem(
        "Show that any positive odd integer is of the form 6q + 1, or 6q + 3, or 6q + 5.",
        "Let 'a' be any positive integer and b = 6.\nBy Euclid’s algorithm, a = 6q + r for some integer q ≥ 0, and r = 0, 1, 2, 3, 4, 5 because 0 ≤ r < 6.\n\nTherefore, 'a' can be 6q, 6q+1, 6q+2, 6q+3, 6q+4, or 6q+5.\nAlso, 6q+1 = 2(3q) + 1 = 2k + 1, where k is an integer.\nSimilarly 6q+3 and 6q+5 are not divisible by 2.\n\nTherefore, any positive odd integer is of the form 6q + 1, or 6q + 3, or 6q + 5."
    ),
    QAItem(
        "Prove that √5 is irrational.",
        "Let us assume, to the contrary, that √5 is rational.\nThen we can find coprime integers a and b (b ≠ 0) such that √5 = a/b.\n\nRearranging, we get a = b√5.\nSquaring on both sides, a² = 5b².\nTherefore, 5 divides a². It follows that 5 divides a.\n\nLet a = 5c for some integer c.\nSubstituting a, we get 25c² = 5b², or b² = 5c².\nThis means 5 divides b², and so 5 divides b.\n\nTherefore, a and b have at least 5 as a common factor.\nBut this contradicts the fact that a and b have no common factors other than 1.\nThis contradiction has arisen because of our incorrect assumption that √5 is rational.\nSo, we conclude that √5 is irrational."
    )
)

@Composable
fun ChapterQAScreen() {
    Scaffold(
        containerColor = SoftBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Aesthetic Header
            item {
                HeaderSection(
                    chapterNumber = "01",
                    chapterName = "Real Numbers",
                    subject = "Class 10 Mathematics"
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 2. Question List
            itemsIndexed(chapter1Questions) { index, item ->
                ExpandableQACard(
                    index = index + 1,
                    qaItem = item
                )
            }
        }
    }
}

@Composable
fun HeaderSection(chapterNumber: String, chapterName: String, subject: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryAccent, Color(0xFF8E84FF))
                )
            )
    ) {
        // Decorative Circles for aesthetics
        Box(
            modifier = Modifier
                .offset(x = (-40).dp, y = (-40).dp)
                .size(150.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 20.dp, y = 40.dp)
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(24.dp)
        ) {
            Text(
                text = subject.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.8f),
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Chapter $chapterNumber",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = chapterName,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun ExpandableQACard(index: Int, qaItem: QAItem) {
    var expanded by remember { mutableStateOf(false) }

    // Card Container
    ElevatedCard(
        onClick = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = CardSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Question Row
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Q. Badge
                Text(
                    text = "Q$index",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = QuestionLabelColor,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .background(QuestionLabelColor.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))

                // Question Text
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = qaItem.question,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = SecondaryAccent,
                        lineHeight = 22.sp
                    )
                }

                // Expand/Collapse Icon
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = TextGray
                )
            }

            // Answer Section (Animated)
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Divider(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .fillMaxWidth(),
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )
                    
                    Row(verticalAlignment = Alignment.Top) {
                        // Vertical Line Decorator
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(24.dp) // Initial height hint
                                .background(PrimaryAccent, CircleShape)
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = qaItem.answer,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextGray,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        }
    }
}

// --- Preview ---
@Preview(showBackground = true)
@Composable
fun PreviewChapterPage() {
    MaterialTheme {
        ChapterQAScreen()
    }
}