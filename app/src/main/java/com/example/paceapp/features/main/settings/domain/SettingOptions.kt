package com.example.paceapp.features.main.settings.domain

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.paceapp.R

enum class SettingOptions(
    @param:StringRes val titleRes: Int,
    @param:DrawableRes val iconRes: Int,
) {
    NOTIFICATIONS(
        titleRes = R.string.notifications,
        iconRes = R.drawable.ic_notifications
    ),

    PRIVACY_POLICY(
        titleRes = R.string.privacy_policy,
        iconRes = R.drawable.ic_privacy_policy
    ),

    TERMS_SERVICE(
        titleRes = R.string.terms_of_service,
        iconRes = R.drawable.ic_terms_service
    ),

    LICENSES(
        titleRes = R.string.licenses,
        iconRes = R.drawable.ic_licenses
    ),

    DEVELOPED_BY(
        titleRes = R.string.developed_by,
        iconRes = R.drawable.ic_developed_by
    );
}