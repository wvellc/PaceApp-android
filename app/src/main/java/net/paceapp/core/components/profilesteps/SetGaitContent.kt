package net.paceapp.core.components.profilesteps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.R
import net.paceapp.core.components.AppDigitPicker
import net.paceapp.core.components.AppSegmentedButtons
import net.paceapp.core.domain.models.GaitPace
import net.paceapp.core.domain.models.GaitUnit
import net.paceapp.core.extensions.g2Continuity
import net.paceapp.core.extensions.titleRes
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun SetGaitContent(
    modifier: Modifier = Modifier,
    runningGait: GaitPace,
    onRunningChange: (GaitPace) -> Unit,
    walkingGait: GaitPace,
    onWalkingChange: (GaitPace) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.set_gait_step_description),
            style = AppTheme.typography.medium.copy(
                color = AppColors.White,
                fontSize = 20.sp,
                lineHeight = 32.sp
            )
        )
        Spacer(Modifier.height(16.dp))
        //Running gait picker
        GaitPickerView(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.running),
            gait = runningGait,
            onGaitChanged = onRunningChange
        )
        Spacer(Modifier.height(24.dp))
        //Walking gait picker
        GaitPickerView(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.walking),
            gait = walkingGait,
            onGaitChanged = onWalkingChange
        )

    }
}

@Composable
private fun GaitPickerView(
    modifier: Modifier = Modifier,
    title: String,
    gait: GaitPace,
    onGaitChanged: (GaitPace) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        //Title
        Text(
            text = title,
            style = AppTheme.typography.medium.copy(
                color = AppColors.White,
                fontSize = 16.sp,
            )
        )

        //PACE unit
        AppSegmentedButtons(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ContinuousRoundedRectangle(12.dp, continuity = g2Continuity))
                .background(AppColors.White.copy(alpha = 0.1f))
                .padding(4.dp),
            segments = GaitUnit.entries,
            selectedSegment = gait.unit,
            itemTitle = { stringResource(it.titleRes) },
            onSegmentSelected = { newUnit ->
                onGaitChanged(gait.copy(unit = newUnit))
            },
        )

        //Digit picker
        AppDigitPicker(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = AppColors.White,
                    shape = ContinuousRoundedRectangle(12.dp)
                )
                .padding(horizontal = 24.dp, vertical = 8.dp),
            initialValue = gait.value.toDouble(),
            range = 0.1..9.0,
            itemSpacing = 4.dp,
            padWithZero = true,
            step = 0.1,
            onValueChange = { newValue ->
                onGaitChanged(gait.copy(value = newValue.toFloat()))
            }
        )
    }
}