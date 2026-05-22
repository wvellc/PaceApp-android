package net.paceapp.core.components.profilesteps

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import net.paceapp.R
import net.paceapp.core.components.NoDataView
import net.paceapp.theme.AppTheme

@Composable
fun PairWatchInitContent(
    modifier: Modifier = Modifier,
    showButton: Boolean = false,
    onButtonClick: () -> Unit = {},
) {
    NoDataView(
        modifier = modifier,
        imageRes = R.drawable.ic_pair_watch,
        title = stringResource(R.string.pair_watch_title),
        subtitle = stringResource(R.string.pair_watch_subtitle),
        buttonLabel = when {
            showButton -> stringResource(R.string.profile_step_start_pairing)
            else -> null
        },
        onButtonClick = onButtonClick
    )
}