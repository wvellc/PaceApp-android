package com.example.paceapp.features.authentication.buildprofile.steps

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.paceapp.R
import com.example.paceapp.core.components.NoDataView
import com.garmin.android.connectiq.IQDevice

@Composable
fun PairWatchContent() {
    NoDataView(
        modifier = Modifier
            .padding(16.dp)
            .wrapContentSize(),
        imageRes = R.drawable.ic_pair_watch,
        title = stringResource(R.string.pair_watch_title),
        subtitle = stringResource(R.string.pair_watch_subtitle),
    )
}