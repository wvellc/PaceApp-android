package net.paceapp.core.utils

import androidx.compose.ui.graphics.Color
import net.paceapp.BuildConfig
import net.paceapp.theme.AppColors
import java.net.URLEncoder
import kotlin.math.roundToInt

object StaticMapHelper {

    private const val BASE_URL = "https://maps.googleapis.com/maps/api/staticmap"

    /**
     * Generates a Google Static Maps URL.
     * @param encodedPolyline The Google-encoded polyline string.
     * @param width The desired width of the image in pixels.
     * @param height The desired height of the image in pixels.
     * @param pathColor Hex color for the route line
     * @param pathWeight The thickness of the route line.
     */
    fun buildPolylineMapUrl(
        encodedPolyline: String,
        width: Int = 600,
        height: Int = 300,
        pathColor: String = AppColors.NeonAquaBlue.toStaticMapsHex(),
        pathWeight: Int = 5,
        visibleArea: String? = null
    ): String {
        val apiKey = BuildConfig.STATIC_MAPS_API_KEY

        val safePolyline = URLEncoder.encode(encodedPolyline, "UTF-8")

        val pathParam = "color:$pathColor|weight:$pathWeight|enc:$safePolyline"

        val visibleParam = visibleArea?.let {
            "&visible=${URLEncoder.encode(it, "UTF-8")}"
        } ?: ""

        val styleParam = "style=feature:all|saturation:-100|lightness:10"

        return buildString {
            append(BASE_URL)
            append("?size=${width}x${height}")
            append("&scale=2")
            append("&path=$pathParam")
            append(visibleParam)
            append("&$styleParam")
            append("&key=$apiKey")
        }
    }
}

/**
 * Converts a Compose Color to a Google Static Maps compatible Hex String.
 * Google expects 0xRRGGBB or 0xRRGGBBAA (Alpha at the end).
 */
fun Color.toStaticMapsHex(): String {
    val r = (this.red * 255).roundToInt()
    val g = (this.green * 255).roundToInt()
    val b = (this.blue * 255).roundToInt()
    val a = (this.alpha * 255).roundToInt()

    return if (a == 255) {
        // Fully opaque: Use 24-bit format
        String.format("0x%02X%02X%02X", r, g, b)
    } else {
        // Transparent: Use 32-bit format (RRGGBBAA)
        String.format("0x%02X%02X%02X%02X", r, g, b, a)
    }
}