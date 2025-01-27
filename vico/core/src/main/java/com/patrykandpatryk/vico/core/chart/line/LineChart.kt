/*
 * Copyright 2022 Patryk Goworowski and Patryk Michalik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.patrykandpatryk.vico.core.chart.line

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.patrykandpatryk.vico.core.DefaultDimens
import com.patrykandpatryk.vico.core.axis.model.MutableChartModel
import com.patrykandpatryk.vico.core.chart.BaseChart
import com.patrykandpatryk.vico.core.chart.draw.ChartDrawContext
import com.patrykandpatryk.vico.core.chart.forEachIn
import com.patrykandpatryk.vico.core.chart.insets.Insets
import com.patrykandpatryk.vico.core.chart.line.LineChart.LineSpec
import com.patrykandpatryk.vico.core.chart.put
import com.patrykandpatryk.vico.core.chart.segment.MutableSegmentProperties
import com.patrykandpatryk.vico.core.chart.segment.SegmentProperties
import com.patrykandpatryk.vico.core.component.Component
import com.patrykandpatryk.vico.core.component.shape.extension.horizontalCubicTo
import com.patrykandpatryk.vico.core.component.shape.shader.DynamicShader
import com.patrykandpatryk.vico.core.context.DrawContext
import com.patrykandpatryk.vico.core.context.MeasureContext
import com.patrykandpatryk.vico.core.context.layoutDirectionMultiplier
import com.patrykandpatryk.vico.core.entry.ChartEntry
import com.patrykandpatryk.vico.core.entry.ChartEntryModel
import com.patrykandpatryk.vico.core.extension.getRepeating
import com.patrykandpatryk.vico.core.extension.getStart
import com.patrykandpatryk.vico.core.extension.half
import com.patrykandpatryk.vico.core.extension.orZero
import com.patrykandpatryk.vico.core.extension.rangeWith
import com.patrykandpatryk.vico.core.marker.Marker
import kotlin.math.abs
import kotlin.math.min

/**
 * [LineChart] displays data as a continuous line.
 *
 * @param lines a [List] of [LineSpec]s defining the style of each line.
 * @param spacingDp the spacing between each [LineSpec.point] in dp.
 */
public open class LineChart(
    public var lines: List<LineSpec> = listOf(LineSpec()),
    public var spacingDp: Float = DefaultDimens.POINT_SPACING,
) : BaseChart<ChartEntryModel>() {

    /**
     * @param lineColor the color of the line.
     * @param lineThicknessDp the thickness of the line in dp.
     * @param lineBackgroundShader an optional [DynamicShader] that can style the space between the line and the x-axis.
     * @param lineCap the stroke cap for the line.
     * @param cubicStrength the strength of the cubic bezier curve between each key point on the line.
     * @param point an optional [LineSpec] that can be drawn at a given point above the line.
     * @param pointSizeDp the size of the [point] in dp.
     */
    public open class LineSpec(
        lineColor: Int = Color.LTGRAY,
        public var lineThicknessDp: Float = DefaultDimens.LINE_THICKNESS,
        public var lineBackgroundShader: DynamicShader? = null,
        public var lineCap: Paint.Cap = Paint.Cap.ROUND,
        public var cubicStrength: Float = 1f,
        public var point: Component? = null,
        public var pointSizeDp: Float = DefaultDimens.POINT_SIZE,
    ) {

        /**
         * Returns true if the [lineBackgroundShader] is not null, and false otherwise.
         */
        public val hasLineBackgroundShader: Boolean
            get() = lineBackgroundShader != null

        protected val linePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = lineColor
            strokeCap = lineCap
        }

        protected val lineBackgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        /**
         * The color of the line.
         */
        public var lineColor: Int by linePaint::color

        /**
         * The stroke cap for the line.
         */
        public var lineStrokeCap: Paint.Cap by linePaint::strokeCap

        /**
         * Draws a [point] at the given [x] and [y] coordinates, using the provided [context].
         *
         * @see Component
         */
        public fun drawPoint(
            context: DrawContext,
            x: Float,
            y: Float,
        ): Unit = with(context) {
            point?.drawPoint(context, x, y, pointSizeDp.pixels.half)
        }

        /**
         * Draws the chart line using the given [context] and the [path].
         */
        public fun drawLine(context: DrawContext, path: Path): Unit = with(context) {
            linePaint.strokeWidth = lineThicknessDp.pixels
            canvas.drawPath(path, linePaint)
        }

        /**
         * Draws a background of the chart line using the given [context] and the [path].
         */
        public fun drawBackgroundLine(context: DrawContext, bounds: RectF, path: Path): Unit = with(context) {
            lineBackgroundPaint.shader = lineBackgroundShader
                ?.provideShader(
                    context = context,
                    left = bounds.left,
                    top = bounds.top,
                    right = bounds.right,
                    bottom = bounds.bottom,
                )

            canvas.drawPath(path, lineBackgroundPaint)
        }
    }

    private val linePath = Path()
    private val lineBackgroundPath = Path()

    private val segmentProperties = MutableSegmentProperties()

    override val entryLocationMap: HashMap<Float, MutableList<Marker.EntryModel>> = HashMap()

    override fun drawChart(
        context: ChartDrawContext,
        model: ChartEntryModel,
    ): Unit = with(context) {
        resetTempData()

        val (cellWidth, spacing, _) = segmentProperties

        model.entries.forEachIndexed { index, entries ->

            linePath.rewind()
            lineBackgroundPath.rewind()
            val component = lines.getRepeating(index)

            var cubicCurvature: Float
            var prevX = bounds.getStart(isLtr = isLtr)
            var prevY = bounds.bottom

            val drawingStart = bounds.getStart(isLtr = isLtr) +
                layoutDirectionMultiplier * (spacing.half + cellWidth.half) - horizontalScroll

            model.forEachPointWithinBounds(entries, segmentProperties, drawingStart, this) { entry, x, y ->
                if (linePath.isEmpty) {
                    linePath.moveTo(x, y)
                    if (component.hasLineBackgroundShader) {
                        lineBackgroundPath.moveTo(x, bounds.bottom)
                        lineBackgroundPath.lineTo(x, y)
                    }
                } else {
                    cubicCurvature = spacing * component.cubicStrength *
                        min(1f, abs((y - prevY) / bounds.bottom) * CUBIC_Y_MULTIPLIER)
                    linePath.horizontalCubicTo(prevX, prevY, x, y, cubicCurvature, isLtr)
                    if (component.hasLineBackgroundShader) {
                        lineBackgroundPath.horizontalCubicTo(prevX, prevY, x, y, cubicCurvature, isLtr)
                    }
                }
                prevX = x
                prevY = y

                if (x in bounds.left..bounds.right) {
                    entryLocationMap.put(
                        x = x,
                        y = y.coerceIn(bounds.top, bounds.bottom),
                        entry = entry,
                        color = component.lineColor,
                    )
                }
            }

            if (component.hasLineBackgroundShader) {
                lineBackgroundPath.lineTo(prevX, bounds.bottom)
                lineBackgroundPath.close()
                component.drawBackgroundLine(context, bounds, lineBackgroundPath)
            }
            component.drawLine(context, linePath)

            if (component.point != null) {
                model.forEachPointWithinBounds(entries, segmentProperties, drawingStart, this) { _, x, y ->
                    component.drawPoint(context, x, y)
                }
            }
        }
    }

    private fun resetTempData() {
        entryLocationMap.clear()
        linePath.rewind()
        lineBackgroundPath.rewind()
    }

    private fun ChartEntryModel.forEachPointWithinBounds(
        entries: List<ChartEntry>,
        segment: SegmentProperties,
        drawingStart: Float,
        context: DrawContext,
        action: (entry: ChartEntry, x: Float, y: Float) -> Unit,
    ) {
        var x: Float
        var y: Float

        var prevEntry: ChartEntry? = null
        var lastEntry: ChartEntry? = null

        val chartMinY = this@LineChart.minY.orZero
        val boundsStart = bounds.getStart(isLtr = context.isLtr)
        val boundsEnd = boundsStart + context.layoutDirectionMultiplier * bounds.width()

        val heightMultiplier = bounds.height() / (drawMaxY - chartMinY)

        fun getDrawX(entry: ChartEntry): Float = drawingStart + context.layoutDirectionMultiplier *
            (segment.cellWidth + segment.marginWidth) * (entry.x - drawMinX) / stepX

        fun getDrawY(entry: ChartEntry): Float =
            bounds.bottom - (entry.y - chartMinY) * heightMultiplier

        entries.forEachIn(drawMinX - stepX..drawMaxX + stepX) { entry ->
            x = getDrawX(entry)
            y = getDrawY(entry)
            when {
                context.isLtr && x < boundsStart || context.isLtr.not() && x > boundsStart -> {
                    prevEntry = entry
                }
                x in boundsStart.rangeWith(other = boundsEnd) -> {
                    prevEntry?.also {
                        action(it, getDrawX(it), getDrawY(it))
                        prevEntry = null
                    }
                    action(entry, x, y)
                }
                (context.isLtr && x > boundsEnd || context.isLtr.not() && x < boundsEnd) && lastEntry == null -> {
                    action(entry, x, y)
                    lastEntry = entry
                }
            }
        }
    }

    override fun getSegmentProperties(
        context: MeasureContext,
        model: ChartEntryModel,
    ): SegmentProperties = with(context) {
        segmentProperties.set(
            cellWidth = lines.maxOf { it.pointSizeDp.pixels },
            marginWidth = spacingDp.pixels,
        )
    }

    override fun setToChartModel(chartModel: MutableChartModel, model: ChartEntryModel) {
        chartModel.minY = minY ?: min(model.minY, 0f)
        chartModel.maxY = maxY ?: model.maxY
        chartModel.minX = minX ?: model.minX
        chartModel.maxX = maxX ?: model.maxX
        chartModel.chartEntryModel = model
    }

    override fun getInsets(context: MeasureContext, outInsets: Insets): Unit = with(context) {
        outInsets.setVertical(lines.maxOf { it.lineThicknessDp.pixels })
    }

    private companion object {
        const val CUBIC_Y_MULTIPLIER = 4
    }
}
