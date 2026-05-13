package com.example.paceapp.features.authentication.buildprofile.steps

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.paceapp.R
import com.example.paceapp.core.components.NoDataView
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.core.garmin.models.WatchModel
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.wvelabs.core_ui.components.AppNetworkImage
import com.wvelabs.core_ui.extensions.advancedShadow

@Composable
fun SelectModelContent(
    watchList: List<WatchModel>,
    selectedWatch: WatchModel?,
    onModelTap: (WatchModel) -> Unit = {}
) {

    if (watchList.isEmpty()) {
        NoDataView(
            title = "No deices found"
        )
        return
    }
    val watchListState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = watchListState,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        val shape = RoundedCornerShape(8.dp)
        items(watchList, key = { it.id }) { device ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .advancedShadow(
                        color = AppColors.Black,
                        cornersRadius = 8.dp,
                        alpha = 0.25f,
                        shadowBlurRadius = 0.25f,
                        offsetY = 4f
                    )
                    .clip(shape)

                    .background(color = AppColors.White)
                    .defaultClickable {
                        onModelTap(device)
                    }
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppNetworkImage(
                    modifier = Modifier, imageUrl = "",
                    placeholder = painterResource(R.drawable.ic_watch_placeholder),
                    error = painterResource(R.drawable.ic_watch_placeholder),
                    shape = CircleShape,
                    size = 80.dp
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),

                    verticalArrangement = Arrangement.spacedBy(
                        4.dp,
                        alignment = Alignment.CenterVertically
                    )
                ) {
                    Text(
                        device.name,
                        style = AppTheme.typography.size24.copy(
                            color = AppColors.DarkCharcoal, fontWeight = FontWeight.SemiBold,

                            )
                    )
                    if (device.model != null) {
                        Text(
                            device.model,
                            style = AppTheme.typography.size16.copy(
                                color = AppColors.NeonAquaBlue, fontWeight = FontWeight.Normal,
                            )
                        )
                    }
                }

                AnimatedContent(
                    targetState = selectedWatch?.id == device.id,
                    transitionSpec = {
                        // The new icon springs in while scaling up and fading in
                        val enter =
                            scaleIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn()
                        // The old icon scales down and fades out
                        val exit =
                            scaleOut(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut()
                        enter togetherWith exit
                    },
                    label = "IconSwitchAnimation"
                ) { isSelected ->
                    Image(
                        painter = painterResource(
                            id = when {
                                isSelected -> R.drawable.ic_check_selected
                                else -> R.drawable.ic_check
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier,
                    )
                }

            }
        }
    }
}