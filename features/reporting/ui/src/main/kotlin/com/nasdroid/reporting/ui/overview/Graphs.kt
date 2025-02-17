package com.nasdroid.reporting.ui.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.boswelja.bitrate.BitrateUnit
import com.boswelja.capacity.CapacityUnit
import com.boswelja.percentage.PercentageStyle
import com.boswelja.temperature.TemperatureUnit
import com.nasdroid.design.MaterialThemeExt
import com.nasdroid.reporting.logic.graph.BitrateGraph
import com.nasdroid.reporting.logic.graph.CapacityGraph
import com.nasdroid.reporting.logic.graph.DurationGraph
import com.nasdroid.reporting.logic.graph.FloatGraph
import com.nasdroid.reporting.logic.graph.Graph
import com.nasdroid.reporting.logic.graph.PercentageGraph
import com.nasdroid.reporting.logic.graph.TemperatureGraph
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sign
import kotlin.time.DurationUnit

/**
 * Composable function that displays a variety of graphs based on the type of [Graph] provided.
 *
 * @param graph Instance of the sealed class [Graph]. This can be any subtype of [Graph], such as
 * [BitrateGraph], [CapacityGraph], etc.
 * @param modifier Modifier to be applied to this composable. Defaults to [Modifier] if not provided.
 */
@Composable
fun Graph(
    graph: Graph<*>,
    modifier: Modifier = Modifier
) {
    when (graph) {
        is BitrateGraph -> BitrateGraph(graph = graph, modifier = modifier)
        is CapacityGraph -> CapacityGraph(graph = graph, modifier = modifier)
        is DurationGraph -> DurationGraph(graph = graph, modifier = modifier)
        is PercentageGraph -> PercentageGraph(graph = graph, modifier = modifier)
        is TemperatureGraph -> TemperatureGraph(graph = graph, modifier = modifier)
        is FloatGraph -> FloatGraph(graph = graph, modifier = modifier)
    }
}

/**
 * Composable function that displays a [BitrateGraph]. This graph shows data in terms of Mibibyte/s.
 *
 * @param graph Instance of the subtype [BitrateGraph] representing bitrate data.
 * @param modifier Modifier to be applied to this composable. Defaults to [Modifier] if not provided.
 */
@Composable
fun BitrateGraph(
    graph: BitrateGraph,
    modifier: Modifier = Modifier
) {
    VicoGraph(
        graph = graph,
        dataTransform = { it.toDouble(BitrateUnit.MEBIBITS) },
        verticalLabel = "Mibibyte/s",
        modifier = modifier
    )
}

/**
 * Composable function that displays a [CapacityGraph]. This graph shows data in terms of Gigabytes.
 *
 * @param graph Instance of the subtype [CapacityGraph] representing capacity data.
 * @param modifier Modifier to be applied to this composable. Defaults to [Modifier] if not provided.
 */
@Composable
fun CapacityGraph(
    graph: CapacityGraph,
    modifier: Modifier = Modifier
) {
    VicoGraph(
        graph = graph,
        dataTransform = { it.toDouble(CapacityUnit.GIGABYTE) },
        verticalLabel = "Gigabytes",
        modifier = modifier
    )
}

/**
 * Composable function that displays a [DurationGraph]. This graph shows data in terms of Days.
 *
 * @param graph Instance of the subtype [DurationGraph] representing duration data.
 * @param modifier Modifier to be applied to this composable. Defaults to [Modifier] if not provided.
 */
@Composable
fun DurationGraph(
    graph: DurationGraph,
    modifier: Modifier = Modifier
) {
    VicoGraph(
        graph = graph,
        dataTransform = { it.toDouble(DurationUnit.DAYS) },
        verticalLabel = "Days",
        modifier = modifier
    )
}

/**
 * Composable function that displays an [FloatGraph]. This graph shows data in terms of
 * Events per second.
 *
 * @param graph Instance of the subtype [FloatGraph] representing events per second.
 * @param modifier Modifier to be applied to this composable. Defaults to [Modifier] if not provided.
 */
@Composable
fun FloatGraph(
    graph: FloatGraph,
    modifier: Modifier = Modifier
) {
    VicoGraph(
        graph = graph,
        dataTransform = { it },
        verticalLabel = graph.verticalLabel,
        modifier = modifier
    )
}

/**
 * Composable function that displays a [PercentageGraph]. This graph shows data in terms of
 * percentage (%).
 *
 * @param graph Instance of the subtype [PercentageGraph] representing percentage data.
 * @param modifier Modifier to be applied to this composable. Defaults to [Modifier] if not provided.
 */
@Composable
fun PercentageGraph(
    graph: PercentageGraph,
    modifier: Modifier = Modifier
) {
    VicoGraph(
        graph = graph,
        dataTransform = { it.toDouble(PercentageStyle.FULL) },
        verticalLabel = "%",
        verticalAxisValueFormatter = { value, _, _ ->
            "%.2f".format(value)
        },
        modifier = modifier
    )
}

/**
 * Composable function that displays a [TemperatureGraph]. This graph shows data in terms of Celsius
 * (°C).
 *
 * @param graph Instance of the subtype [TemperatureGraph] representing temperature
 * data.
 * @param modifier Modifier to be applied to this composable. Defaults to [Modifier] if not provided.
 */
@Composable
fun TemperatureGraph(
    graph: TemperatureGraph,
    modifier: Modifier = Modifier
) {
    VicoGraph(
        graph = graph,
        dataTransform = { it.toDouble(TemperatureUnit.CELSIUS) },
        verticalLabel = "°C",
        modifier = modifier,
        constraintAtZero = false
    )
}

@Composable
@Suppress("LongMethod") // We don't have much of a choice here unfortunately.
internal fun <T> VicoGraph(
    graph: Graph<T>,
    dataTransform: (T) -> Number,
    verticalLabel: String,
    modifier: Modifier = Modifier,
    verticalAxisValueFormatter: CartesianValueFormatter = remember { CartesianValueFormatter.decimal() },
    constraintAtZero: Boolean = true
) {
    val modelProducer = remember {
        CartesianChartModelProducer()
    }
    val timeFormatter = remember {
        LocalDateTime.Format {
            hour()
            char(':')
            minute()
        }
    }
    val theme = vicoTheme
    val legendTextComponent = rememberTextComponent(
        color = MaterialThemeExt.colorScheme.onSurface
    )
    LaunchedEffect(graph, dataTransform) {
        modelProducer.runTransaction {
            lineSeries {
                val lines = graph.dataSlices.first().data.size
                (0 until lines).map { lineIndex ->
                    series(graph.dataSlices.map { dataTransform(it.data[lineIndex]) })
                }
            }
        }
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialThemeExt.paddings.medium)
    ) {
        Text(
            text = graph.name,
            style = MaterialThemeExt.typography.titleMedium,
        )

        ProvideVicoTheme(theme = rememberM3VicoTheme()) {
            CartesianChartHost(
                modelProducer = modelProducer,
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        rangeProvider = remember(constraintAtZero) {
                            if (constraintAtZero) {
                                CartesianLayerRangeProvider.auto()
                            } else {
                                autoNoConstraints()
                            }
                        },
                    ),
                    startAxis = VerticalAxis.rememberStart(
                        title = verticalLabel,
                        titleComponent = rememberTextComponent(),
                        valueFormatter = verticalAxisValueFormatter,
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = { _, value, _ ->
                            graph.dataSlices.getOrNull(value.toInt())?.timestamp
                                ?.toLocalDateTime(TimeZone.currentSystemDefault())
                                ?.let {
                                    timeFormatter.format(it)
                                }
                                .orEmpty()
                        },
                        itemPlacer = remember { HorizontalAxis.ItemPlacer.aligned(spacing = { 2 }) }
                    ),
                    legend = rememberHorizontalLegend(
                        items = {
                            graph.legend.mapIndexed { index, legend ->
                                add(
                                    LegendItem(
                                        icon = shapeComponent(
                                            fill = fill(
                                                theme.lineCartesianLayerColors[
                                                    index % theme.lineCartesianLayerColors.size
                                                ]
                                            )
                                        ),
                                        labelComponent = legendTextComponent,
                                        label = legend
                                    )
                                )
                            }
                        },
                        iconSize = 8.dp,
                        padding = Insets(MaterialThemeExt.paddings.tiny.value),
                        columnSpacing = MaterialThemeExt.paddings.medium,
                        rowSpacing = MaterialThemeExt.paddings.medium
                    )
                ),
                scrollState = rememberVicoScrollState(initialScroll = Scroll.Absolute.End),
                animateIn = false,
                zoomState = rememberVicoZoomState(false)
            )
        }
    }
}

internal fun autoNoConstraints(): CartesianLayerRangeProvider =
    object : CartesianLayerRangeProvider {
        override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore): Double = when {
            minY == 0.0 && maxY == 0.0 -> 0.0
            else -> minY.round(maxY)
        }

        override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore): Double = when {
            minY == 0.0 && maxY == 0.0 -> 1.0
            else -> maxY.round(minY)
        }

        private fun Double.round(other: Double): Double {
            val absoluteValue = abs(this)
            val base = 10.0.pow(floor(log10(max(absoluteValue, abs(other)))) - 1)
            return sign * ceil(absoluteValue / base) * base
        }
    }
