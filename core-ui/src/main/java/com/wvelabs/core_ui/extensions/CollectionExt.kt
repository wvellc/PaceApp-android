package com.wvelabs.core_ui.extensions

// Prevents the classic IndexOutOfBoundsException
fun <T> List<T>?.getOrNull(index: Int): T? = 
    if (this != null && index in indices) this[index] else null

// Useful for updating an item in a State list (MVI pattern)
fun <T> List<T>.updateItem(predicate: (T) -> Boolean, transform: (T) -> T): List<T> {
    return map { if (predicate(it)) transform(it) else it }
}