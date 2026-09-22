package net.paceapp.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import net.paceapp.core.models.AnalyticsDataPoint
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.DashedShape
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.Insets
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.wvelabs.core_ui.extensions.defaultAnimSpec


// Rounds a rough step up to a "nice" 1/2/5×10ⁿ value so the y-grid uses tidy intervals
// and only a few sections regardless of the data's magnitude.
private fun niceStep(rough: Double): Double {
    if (rough <= 0.0 || rough.isNaN() || rough.isInfinite()) return 1.0
    val magnitude = 10.0.pow(floor(log10(rough)))
    val residual = rough / magnitude
    val niceResidual = when {
        residual <= 1.0 -> 1.0
        residual <= 2.0 -> 2.0
        residual <= 5.0 -> 5.0
        else -> 10.0
    }
    return niceResidual * magnitude
}

@Composable
fun AppLineChart(
    dataPoints: List<AnalyticsDataPoint>,
    lineColor: Color,
    modifier: Modifier = Modifier,
    markersEnabled: Boolean = false,
    yAxisStep: Double = 20.0,
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    //Map points from model
    LaunchedEffect(dataPoints) {
        modelProducer.runTransaction {
            lineSeries { series(dataPoints.map { it.value }) }
        }
    }

    //Common line component for guidelines and axis
    val guideline = rememberLineComponent(
        thickness = 0.66.dp,
        strokeThickness = 0.66.dp,
        fill = Fill(AppColors.LightGray),
    )

    // Dynamic Y bounds with a "nice" step chosen from the data so the grid has only a few
    // sections (mirrors iOS's ~4–5 gridlines) rather than a fixed step that multiplies
    // lines on large ranges. The step targets ~4 sections and snaps to 1/2/5×10ⁿ; lo/hi
    // are floored/ceiled to it (so gridlines land on round values and the line fills the
    // height), plus a touch of top headroom so the peak doesn't sit on the top line.
    val yBounds = remember(dataPoints) {
        val values = dataPoints.map { it.value }
        if (values.isEmpty()) {
            null
        } else {
            val rawMin = values.min()
            val rawMax = values.max()
            val step = niceStep((rawMax - rawMin) / 4.0)
            val lo = floor(rawMin / step) * step
            var hi = ceil(rawMax / step) * step
            if (hi - rawMax < step * 0.2) hi += step
            if (hi <= lo) hi = lo + step
            Triple(lo, hi, step)
        }
    }
    val rangeProvider = remember(yBounds) {
        yBounds?.let { (lo, hi, _) ->
            CartesianLayerRangeProvider.fixed(minY = lo, maxY = hi)
        } ?: CartesianLayerRangeProvider.auto()
    }
    // Draw a horizontal gridline at every "nice" step. The step placer (unlike the count
    // placer) works even though the start-axis labels are hidden.
    val yStep = yBounds?.third ?: yAxisStep
    val currentYStep by rememberUpdatedState(newValue = yStep)
    val itemPlacer = remember {
        VerticalAxis.ItemPlacer.step({ currentYStep })
    }

    // Tooltip Bubble
    val markerLabelBackground = rememberShapeComponent(
        fill = Fill(AppColors.White),
        strokeFill = Fill(AppColors.DarkCharcoal),
        strokeThickness = 1.dp,
        shape = RoundedCornerShape(3.dp)
    )

    //Marker label
    val markerLabel = rememberTextComponent(
        background = markerLabelBackground,
        style = AppTheme.typography.semiBold.copy(
            fontSize = 11.sp,
            color = AppColors.DarkCharcoal,
        ),
        padding = Insets(horizontal = 8.dp, vertical = 4.dp),
    )

    // The Indicator Dot
    val markerIndicator = rememberShapeComponent(
        fill = Fill(lineColor),
        strokeThickness = 0.dp,
        shape = CircleShape
    )

    // The Drag Line
    val markerGuideline = rememberLineComponent(
        fill = Fill(AppColors.FashionGray),
        thickness = 1.dp,
        shape = remember {
            DashedShape(
                shape = RectangleShape,
                dashLength = 3.dp,
                gapLength = 3.dp,
            )
        }
    )

    // Assemble the Marker
    val chartMarker = rememberDefaultCartesianMarker(
        label = markerLabel,
        valueFormatter = { _, targets ->
            targets.joinToString("\n") { target ->
                // Trim the raw double to 2 decimals (e.g. 19.51361616 → "19.51%").
                String.format(Locale.US, "%.2f%%", dataPoints[target.x.toInt()].value)
            }
        },
        indicator = { markerIndicator },
        guideline = markerGuideline
    )
    // Lock the chart: no panning, no pinch-zoom. Disabling scroll also disables zoom
    // (the host gates zoom on scrollEnabled) and makes the default zoom state fit all
    // points to the full width via Zoom.Content, so data is shown in full, never cut off.
    val scrollState = rememberVicoScrollState(scrollEnabled = false)

    // Chart + its own x-axis label row. The chart plots edge-to-edge (no extreme label
    // padding), and the labels are laid out manually below with SpaceBetween so the first
    // hugs the left and the last hugs the right (iOS-style), instead of Vico insetting the
    // plot to fit centred edge labels (which left an empty strip before the first point).
    Column(modifier = modifier) {
    //Chart host
    CartesianChartHost(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        modelProducer = modelProducer,
        scrollState = scrollState,
        animationSpec = defaultAnimSpec(duration = 200),
        //Chart
        chart = rememberCartesianChart(

            //Layers
            rememberLineCartesianLayer(
                rangeProvider = rangeProvider,

                //Lines
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(Fill(lineColor)),
                        interpolator = LineCartesianLayer.Interpolator.cubic(),
                        areaFill = LineCartesianLayer.AreaFill.single(
                            fill = Fill(
                                Brush.verticalGradient(
                                    listOf(
                                        lineColor.copy(alpha = 0.4f),
                                        lineColor.copy(alpha = 0.0f)
                                    )
                                )
                            )
                        )
                    )
                )
            ),

            startAxis = VerticalAxis.rememberStart(
                // Left-axis value labels removed; keep the horizontal guidelines only.
                label = null,
                guideline = guideline,
                tick = guideline,
                line = guideline,
                tickLength = 0.dp,
                itemPlacer = itemPlacer
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                // Labels are drawn by the manual row below; here we only want the bottom
                // line + the vertical gridlines, edge-to-edge (no extreme label padding).
                label = null,
                line = guideline,
                tick = guideline,
                tickLength = 0.dp,
                guideline = guideline,
                itemPlacer = remember {
                    HorizontalAxis.ItemPlacer.aligned(addExtremeLabelPadding = false)
                }
            ),

            marker = when {
                markersEnabled -> chartMarker
                else -> null
            }
        ),
    )

        // Manual x-axis labels, aligned edge-to-edge with the plotted points: first label
        // hugs the left, last hugs the right, the rest spread evenly between.
        if (dataPoints.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                dataPoints.forEach { point ->
                    Text(
                        text = point.label,
                        maxLines = 1,
                        style = AppTheme.typography.medium.copy(
                            fontSize = 9.sp,
                            color = AppColors.Gray,
                        ),
                    )
                }
            }
        }
    }
}