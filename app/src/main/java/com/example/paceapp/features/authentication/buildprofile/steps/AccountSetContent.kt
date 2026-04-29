package com.example.paceapp.features.authentication.buildprofile.steps

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
import com.example.paceapp.core.components.imagepicker.AppImagePicker
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.wvelabs.core_ui.components.AppNetworkImage

@Composable
fun AccountSetContent() {

    var showPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Set up your account to track your pace, performance, and progress in real time.",
            style = AppTheme.typography.size20.copy(
                color = AppColors.White,
                fontWeight = FontWeight.Medium,
                lineHeight = 32.sp
            )
        )

        Spacer(Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 108.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 72.dp,
                        topEnd = 72.dp,
                        bottomEnd = 10.dp,
                        bottomStart = 10.dp
                    )
                )
                .background(color = AppColors.White)
                .defaultClickable {
                    showPicker = true
                },
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_camera),
                contentDescription = null,
            )
            AppNetworkImage(
                imageUrl = "https://img.freepik.com/free-photo/portrait-white-man-isolated_53876-40306.jpg?semt=ais_hybrid&w=740&q=80",
                modifier = Modifier.fillMaxSize(),
            )
        }


        AppImagePicker(
            isVisible = showPicker,
            showReplaceSheet = true,
            onDismiss = { showPicker = false },
            onAction = { action -> },
        )
    }
}

