package net.paceapp.core.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor
import net.paceapp.core.models.AnalyticsDataPoint
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
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

    // X-axis labels come straight from each data point's bucket label so the bottom
    // bar reads as the period's duration: Day → times, Week → weekdays, Month → weeks,
    // Year → months (produced by AnalyticsAggregator.bucketRecords).
    val bottomAxisFormatter = remember(dataPoints) {
        CartesianValueFormatter { _, xValue, _ ->
            dataPoints.getOrNull(xValue.toInt())?.label.orEmpty()
        }
    }
    //Common label component
    val axisLabelComponent = rememberTextComponent(
        style = AppTheme.typography.medium.copy(
            fontSize = 12.sp, color = AppColors.Gray
        ),
        lineCount = 1,
        margins = Insets(end = 8.dp),
        overflow = TextOverflow.Ellipsis,
    )

    // Bottom (duration) axis label — smaller font and no side margin so the period
    // labels (e.g. weekdays / months) fit on one line instead of truncating. A top
    // margin drops the labels clear of the bottom axis line so they don't overlap it.
    val bottomAxisLabelComponent = rememberTextComponent(
        style = AppTheme.typography.medium.copy(
            fontSize = 9.sp, color = AppColors.Gray
        ),
        lineCount = 1,
        margins = Insets(top = 6.dp),
    )

    //Common line component for guidelines and axis
    val guideline = rememberLineComponent(
        thickness = 0.66.dp,
        strokeThickness = 0.66.dp,
        fill = Fill(AppColors.LightGray),
    )

    // Dynamic Y range: fit to whatever data is available instead of a fixed 0–100 span,
    // so a series sitting around ~19% fills the chart height rather than hugging the
    // bottom. Min/max are floored/ceiled to the y-step so the guidelines still land on
    // round values; a flat series gets a one-step span so it isn't zero-height.
    val rangeProvider = remember(dataPoints, yAxisStep) {
        val values = dataPoints.map { it.value }
        if (values.isEmpty()) {
            CartesianLayerRangeProvider.auto()
        } else {
            val step = if (yAxisStep > 0.0) yAxisStep else 1.0
            val rawMax = values.max()
            val lo = floor(values.min() / step) * step
            var hi = ceil(rawMax / step) * step
            // Add a step of headroom when the peak sits on/near the top gridline so the
            // line doesn't touch the top of the grid.
            if (hi - rawMax < step * 0.2) hi += step
            CartesianLayerRangeProvider.fixed(minY = lo, maxY = if (hi > lo) hi else lo + step)
        }
    }
    val currentStep by rememberUpdatedState(newValue = yAxisStep)
    // Dynamic Item Placer based on passed arguments
    val itemPlacer = remember {
        VerticalAxis.ItemPlacer.step({ currentStep })
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

    //Chart host
    CartesianChartHost(
        modifier = modifier,
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
                label = bottomAxisLabelComponent,
                valueFormatter = bottomAxisFormatter,
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
}