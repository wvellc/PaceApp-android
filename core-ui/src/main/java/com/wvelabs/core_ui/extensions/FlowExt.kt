package com.wvelabs.core_ui.extensions

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(FlowPreview::class)
fun <T> Flow<T>.debounceInput(
    timeout: Long = 300L,
) = this
    .debounce(timeout)
    .distinctUntilChanged()
