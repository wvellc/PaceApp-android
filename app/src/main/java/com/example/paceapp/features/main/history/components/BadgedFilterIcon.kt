package com.example.paceapp.features.main.history.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.paceapp.R
import com.example.paceapp.core.extensions.defaultClickable
import com.wvelabs.core_ui.extensions.defaultAnimSpec
import com.wvelabs.core_ui.extensions.defaultScaleIn

@Composable
fun BadgedFilterIcon(
    modifier: Modifier = Modifier,
    hasActiveFilters: Boolean = false,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .defaultClickable { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Filter icon
        Image(
            painter = painterResource(R.drawable.ic_filter),
            contentDescription = "Filter",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        // Custom Image Badge Overlay
        AnimatedVisibility(
            visible = hasActiveFilters,
            modifier = Modifier
                .size(10.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (-2).dp, y = (-4.5).dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_filter_badge),
                contentDescription = "Active filters indicator",
                modifier = Modifier
                    .fillMaxSize()
                    .animateEnterExit(
                        enter = defaultScaleIn() + fadeIn(defaultAnimSpec()),
                    )
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun FilterPreview() = Box(
    modifier = Modifier.size(58.dp),
    contentAlignment = Alignment.Center
) {
    BadgedFilterIcon(
        modifier = Modifier,
        hasActiveFilters = true
    )
}
