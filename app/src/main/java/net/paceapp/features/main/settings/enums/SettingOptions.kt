package net.paceapp.features.main.settings.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import net.paceapp.R

enum class SettingOptions(
    @param:StringRes val titleRes: Int,
    @param:DrawableRes val iconRes: Int,
) {
    STRAVA(
        titleRes = R.string.strava_connect,
        iconRes = R.drawable.ic_sync
    ),

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
//
//    LICENSES(
//        titleRes = R.string.licenses,
//        iconRes = R.drawable.ic_licenses
//    ),

    FAQ(
        titleRes = R.string.faqs,
        iconRes = R.drawable.ic_faq
    ),

    DEVELOPED_BY(
        titleRes = R.string.developed_by,
        iconRes = R.drawable.ic_developed_by
    );
}