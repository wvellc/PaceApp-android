package com.example.paceapp.core.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.core.extensions.tintColor
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.example.paceapp.theme.PaceAppTheme
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.vibrancy
import com.wvelabs.core_ui.alerts.AlertType
import com.wvelabs.core_ui.alerts.MessageType
import com.wvelabs.core_ui.alerts.ToastButton
import com.wvelabs.core_ui.alerts.loadingIcon
import com.wvelabs.core_ui.extensions.advancedShadow

@Composable
fun CustomToast(
    toast: AlertType.Toast,
    onDismiss: () -> Unit = {}
) {

    val toastShape = RoundedCornerShape(14.dp)
    val iconScale = remember { Animatable(1f) }
    LaunchedEffect(toast.id) {
        // Quick "pop" up and then settle back down
        iconScale.animateTo(1.2f, spring(dampingRatio = Spring.DampingRatioHighBouncy))
        iconScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioNoBouncy))
    }

    val toastIcon: @Composable () -> Unit = {
        toast.iconRes?.let { resId ->
            Icon(
                painter = painterResource(id = resId),
                contentDescription = null,
                tint = toast.type.tintColor,
                modifier = Modifier.size(22.dp)
            )
        } ?: Icon(
            imageVector = toast.type.loadingIcon,
            contentDescription = null,
            tint = toast.type.tintColor,
            modifier = Modifier.size(22.dp)
        )
    }

    Box(
        modifier = Modifier
            .wrapContentHeight()
            .advancedShadow(
                cornersRadius = 14.dp,
                color = AppColors.Black,
                shadowBlurRadius = 12f,
                alpha = 0.12f,
                offsetY = 4f
            )
            .widthIn(max = 400.dp)
    ) {

        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(radius = 10.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                .background(
                    color = toast.type.tintColor.copy(alpha = 0.4f),
                    shape = toastShape
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = AppColors.White.copy(alpha = 0.85f),
                    shape = toastShape
                )
                .border(
                    width = 1.dp,
                    color = toast.type.tintColor,
                    shape = toastShape
                )
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Fixed Icon Slot
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .graphicsLayer(scaleX = iconScale.value, scaleY = iconScale.value),
                contentAlignment = Alignment.Center,
            ) {
                toastIcon()
            }

            // Message
            Text(
                text = toast.message,
                color = AppColors.Black,
                textAlign = TextAlign.Center,
                style = AppTheme.typography.size14.copy(fontWeight = FontWeight.Medium),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            )

            // Optional Button
            Box(
                modifier = Modifier.widthIn(min = 22.dp), // Matches icon width for better balance
                contentAlignment = Alignment.CenterEnd
            ) {
                toast.button?.let { btn ->
                    Text(
                        text = btn.title,
                        color = btn.color,
                        style = AppTheme.typography.size14.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.defaultClickable {
                            btn.action()
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}


// Note: Replace these mocks with your actual AlertType.Toast constructors
// if the parameters differ in your core-ui library.

@Preview(showBackground = true, backgroundColor = 0xFF235BFF)
@Composable
fun PreviewCustomToasts() {
    PaceAppTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 1. Success Type (Simple)
            CustomToast(
                toast = AlertType.Toast(
                    id = "1",
                    message = "Task completed successfully!",
                    type = MessageType.Success // Assuming these exist in your enum/sealed class
                )
            )

            // 2. Info Type with Action Button (Tests Centering with Button)
            CustomToast(
                toast = AlertType.Toast(
                    id = "2",
                    message = "New update available.",
                    type = MessageType.Info,
                    button = ToastButton(
                        title = "Update",
                        color = Color(0xFF007AFF), // iOS blue
                        action = {}
                    )
                )
            )

            // 3. Error Type with Long Text (Tests MaxLines & Centering)
            CustomToast(
                toast = AlertType.Toast(
                    id = "3",
                    message = "The connection to the server was lost. Please check your internet settings and try to reconnect again later.",
                    type = MessageType.Error
                )
            )

            // 4. Warning Type (Short text)
            CustomToast(
                toast = AlertType.Toast(
                    id = "4",
                    message = "Battery is running low.",
                    type = MessageType.Warning
                )
            )

            // 5. Loading/No Icon State
            CustomToast(
                toast = AlertType.Toast(
                    id = "5",
                    message = "Syncing data...",
                    type = MessageType.Loading, // Or a dedicated Loading type
                    iconRes = null // This will trigger your loadingIcon logic
                )
            )
        }
    }
}

