package com.wvelabs.core_ui.extensions

fun String?.orDash(): String = if (this.isNullOrBlank()) "-" else this

fun <T> T?.orThrow(message: String = "Required value is null"): T = 
    this ?: throw IllegalStateException(message)

