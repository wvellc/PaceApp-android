package com.example.paceapp.features.main.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.features.main.home.HomeContract.Event
import com.example.paceapp.features.main.home.HomeContract.State
import kotlin.random.Random

@Composable
internal fun HomeContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    val randomColors = remember {
        List(50) {
            Color(
                red = Random.nextFloat(),
                green = Random.nextFloat(),
                blue = Random.nextFloat(),
                alpha = 1f
            )
        }
    }
    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize(),
        isLoading = state.isLoading,
        hasPattern = true,
    ) {innerPaddings->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            // ⚠️ THE CRITICAL "GOTCHA" FIX:
            // Adds bottom padding so the last few items aren't trapped under the glass bar!
            contentPadding = PaddingValues(
                top = innerPaddings.calculateTopPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp,
                // Roughly 80dp for the BottomBar + System Navigation Bar height
                bottom = 80.dp + WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(randomColors.size) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(randomColors[index]),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Glass Test Item #$index",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
