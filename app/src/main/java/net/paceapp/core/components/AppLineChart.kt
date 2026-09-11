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
import kotlin.math.ceil
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
    yAxisFormatter: (Double) -> String = { it.toInt().toString() },
    minY: Double? = null,
    maxY: Double? = null,
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

    //Common label formatter for Y axis
    val startAxisFormatter = remember(yAxisFormatter) {
        CartesianValueFormatter { _, yValue, _ ->
            yAxisFormatter(yValue)
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

    //Common line component for guidelines and axis
    val guideline = rememberLineComponent(
        thickness = 0.66.dp,
        strokeThickness = 0.66.dp,
        fill = Fill(AppColors.LightGray),
    )

    //Min and Max range on y axis. When the data overshoots the requested max (e.g. a
    // Pace Percentage above 100%), raise the top to the next y-step multiple so the
    // point shows in full instead of being drawn/clipped past a hard 100 cap.
    val rangeProvider = remember(minY, maxY, yAxisStep, dataPoints) {
        if (minY != null && maxY != null) {
            val dataMax = dataPoints.maxOfOrNull { it.value } ?: maxY
            val step = if (yAxisStep > 0.0) yAxisStep else 1.0
            val grownMax = ceil(dataMax / step) * step
            CartesianLayerRangeProvider.fixed(minY = minY, maxY = maxOf(maxY, grownMax))
        } else {
            CartesianLayerRangeProvider.auto()
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
                "${dataPoints[target.x.toInt()].value}%"
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
                label = axisLabelComponent,
                valueFormatter = startAxisFormatter,
                guideline = guideline,
                tick = guideline,
                line = guideline,
                tickLength = 0.dp,
                itemPlacer = itemPlacer
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                label = axisLabelComponent,
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