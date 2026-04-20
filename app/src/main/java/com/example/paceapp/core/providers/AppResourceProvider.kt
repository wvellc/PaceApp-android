package com.example.paceapp.core.providers

import android.content.Context
import com.wvelabs.core_ui.resources.ResourceProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class AppResourceProvider @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ResourceProvider {

    override fun getString(resId: Int): String {
        return context.getString(resId)
    }

    override fun getString(resId: Int, vararg args: Any): String {
        return context.getString(resId, *args)
    }

    override fun getQuantityString(
        id: Int,
        quantity: Int,
        vararg formatArgs: Any
    ): String {
        return context.resources.getQuantityString(id, quantity, *formatArgs)
    }

}