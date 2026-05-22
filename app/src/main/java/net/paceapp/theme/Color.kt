package net.paceapp.theme

import androidx.compose.ui.graphics.Color

object AppColors {
    val NeonAquaBlue = Color(0xFF2EB2FF)
    val NeonAquaBlue20 = Color(0x332EB2FF)
    val RadiantBlue = Color(0xFF235BFF)
    val FluorescentMint = Color(0xFF2FF99C)
    val Transparent = Color.Transparent

    val White = Color(0xFFFFFFFF)
    val White20 = Color(0x33FFFFFF)
    val White85 = Color(0xD9FFFFFF)
    val Black = Color(0xFF000000)
    val Black40 = Color(0x66000000)
    val Error = Color(0xFFE43222)
    val InfernoRed = Color(0xFFFF4E36)
    val HintGray = Color(0xFFE5E5E5)
    val FashionGray = Color(0xFF8C8C8C)
    val DarkCharcoal = Color(0xFF323334)
    val LightGray = Color(0xFFF1F1F1)
    val Gray = Color(0xFF7D7D7D)
    val Orange = Color(0xFFFF9500)
    val ShipGray30 = Color(0x4D3C3C43)
    val LimeGreen = Color(0xFF25cd25)

    val backgroundGradient = listOf(
        Color(0xFF0C2D8C),
        Color(0xFF091E5B),
    )
    val borderGradient = listOf(
        FluorescentMint,
        Color(0xFF020302),
    )
    val segmentButtonGradient = listOf(
        Color(0xFF1F97EA),
        Color(0xFF0E76BD)
    )
    val buttonGradient = listOf(
        NeonAquaBlue,
        RadiantBlue,
    )

    val bottomTabBorderGradient = listOf(
        White.copy(alpha = 0.1f),
        FluorescentMint.copy(alpha = 0.6f),
    )

}