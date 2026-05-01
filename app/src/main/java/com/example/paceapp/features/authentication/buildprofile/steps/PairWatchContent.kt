package com.example.paceapp.features.authentication.buildprofile.steps

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
import com.example.paceapp.features.authentication.buildprofile.domain.ProfileStep
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme

@Composable
fun PairWatchContent(step: ProfileStep) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Spacer(modifier = Modifier.fillMaxHeight(0.2f))
        //Logo
        Image(
            painter = painterResource(R.drawable.ic_pair_watch),
            contentDescription = null,
            modifier = Modifier
                .size(180.dp),
        )
        Spacer(modifier = Modifier.height(34.dp))

        //Title
        Text(
            text = stringResource(R.string.pair_watch_title),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            textAlign = TextAlign.Center,
            style = AppTheme.typography.size24.copy(
                color = AppColors.White,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 24.sp,
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        //Subtitle
        Text(
            text = stringResource(R.string.pair_watch_subtitle),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            textAlign = TextAlign.Center,
            style = AppTheme.typography.size16.copy(
                color = AppColors.White,
                fontWeight = FontWeight.Medium,
            )
        )
    }
}