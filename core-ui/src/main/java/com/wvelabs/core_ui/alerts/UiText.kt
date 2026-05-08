package com.wvelabs.core_ui.alerts

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {
    // For when you have an API error or dynamic string
    data class DynamicString(val value: String) : UiText()

    // For when you want to use strings.xml
    class StringResource(
        @param:StringRes val resId: Int,
        vararg val args: Any
    ) : UiText()

    // 🟢 The Magic Extractor: Only the UI calls this!
    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args)
        }
    }
}