package com.wvelabs.core_ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun AppNetworkImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp? = null,
    shape: Shape = RectangleShape,
    border: BorderStroke? = null,
    contentScale: ContentScale = ContentScale.Crop,
    crossfade: Boolean = true,
    backgroundColor: Color = Color.Transparent,
    placeholder: Painter? = null,
    error: Painter? = null
) {

    Box(
        modifier = modifier
            .then(if (size != null) Modifier.size(size) else Modifier)
            .then(if (border != null) Modifier.border(border, shape) else Modifier)
            .clip(shape)
            .background(backgroundColor)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(crossfade)
                .build(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            placeholder = placeholder,
            error = error,
            modifier = Modifier.matchParentSize()
        )
    }
}
