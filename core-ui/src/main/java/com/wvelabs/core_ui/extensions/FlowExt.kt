package com.wvelabs.core_ui.extensions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn

@OptIn(FlowPreview::class)
fun <T> Flow<T>.debounceInput() = this
    .debounce(300L)
    .distinctUntilChanged()
    .flowOn(Dispatchers.Default)