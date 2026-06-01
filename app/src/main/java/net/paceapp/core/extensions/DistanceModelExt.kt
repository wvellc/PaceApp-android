package net.paceapp.core.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import net.paceapp.core.domain.models.DistanceModel
import java.util.Locale

val DistanceModel.displayValue: String
    @Composable get() {
        val formattedValue = if (value % 1.0f == 0f) {
            value.toInt().toString()
        } else {
            String.format(Locale.getDefault(), "%.2f", value)
        }

        return "$formattedValue ${stringResource(unit.titleRes)}"
    }