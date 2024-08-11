/*
 * MIT License
 *
 * Copyright (c) 2024 Randomly Typing
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package rt.yotei.schedule.ui.open

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import rt.yotei.schedule.ext.length
import rt.yotei.schedule.ext.overlaps
import rt.yotei.schedule.ui.ScheduleScope
import rt.yotei.schedule.ui.ScheduleViewport
import kotlin.math.roundToInt

enum class LayoutMode {
  Overlap, Stagger
}

@Composable
fun ScheduleScope.Track(
  modifier: Modifier = Modifier,
  mode: LayoutMode = LayoutMode.Overlap,
  content: @Composable TrackScope.() -> Unit,
) {

  Layout(
    content = { TrackScopeInstance.content() },
    modifier = modifier,
    measurePolicy = { measurables, constraints ->

      val pxPerMinute: Float =
        if (constraints.maxWidth == Constraints.Infinity) {
          1f
        } else {
          constraints.maxWidth.toFloat() / viewport.duration
        }

      when (mode) {
        LayoutMode.Stagger -> staggerMeasure(viewport, pxPerMinute, measurables, constraints)
        else -> overlapMeasure(viewport, pxPerMinute, measurables, constraints)
      }
    },
  )
}

private fun MeasureScope.staggerMeasure(
  viewport: ScheduleViewport,
  pxPerMinute: Float,
  measurables: List<Measurable>,
  constraints: Constraints,
): MeasureResult {
  // region // === Measurement Phase ===

  // Determine groups of overlapping items.
  var currentGroup = mutableListOf<Measurable>()
  val overlapGroups = mutableListOf<List<Measurable>>(currentGroup)
  for (measurable in measurables) {
    if (currentGroup.isEmpty() ||
      currentGroup.last().timeRange?.overlaps(measurable.timeRange) == true
    ) {
      currentGroup.add(measurable)
    } else {
      currentGroup = mutableListOf(measurable)
      overlapGroups.add(currentGroup)
    }
  }

  val placeableGroups: List<List<Pair<Placeable, Long>>> = overlapGroups.map { measurablesGroup ->
    measurablesGroup.mapNotNull { measurable ->
      val timeRange = measurable.timeRange ?: return@mapNotNull null
      val startTime = timeRange.first
      val minutes = timeRange.last - startTime

      val sessionConstraints = Constraints.fixedWidth((minutes * pxPerMinute).roundToInt())

      measurable.measure(sessionConstraints) to startTime
    }
  }

  val contentHeight = placeableGroups.maxOf { placeableGroup ->
    placeableGroup.sumOf { (placeable, _) ->
      placeable.height
    }
  }

  // endregion

  return layout(constraints.maxWidth, constraints.constrainHeight(contentHeight)) {

    // region // === Placement Phase ===

    val viewportRange = viewport.timeRange
    for (placeableGroup in placeableGroups) {
      var y = 0
      for ((placeable, startTime) in placeableGroup) {
        if (placeable.timeRange?.overlaps(viewportRange) == true) {
          val x = ((startTime - viewport.startTime) * pxPerMinute).roundToInt()
          placeable.placeRelative(x = x, y = y)
          y += placeable.height
        }
      }
    }
  }

  // endregion
}

private fun MeasureScope.overlapMeasure(
  viewport: ScheduleViewport,
  pxPerMinute: Float,
  measurables: List<Measurable>,
  constraints: Constraints,
): MeasureResult {

  // region // === Measurement Phase ===

  // Measure all the children using the parent data
  //   provided by our custom modifier.
  val placeables = measurables.map { measurable ->
    // Pull out the parent data.
    val timeRange = measurable.timeRange
    // Calculate the width of the child.
    val durationMinutes = timeRange.length
    val width = (durationMinutes * pxPerMinute).roundToInt()
    // Calculate the height of the child.
    val height = measurable.minIntrinsicHeight(width)
    // Set the width and height using constraints and `measure()`.
    val sessionConstraints = Constraints.fixed(width, height)
    measurable.measure(sessionConstraints)
  }

  // Calculate the schedule's height.
  val contentHeight =
    if (placeables.isNotEmpty()) {
      placeables.maxOf(Placeable::height)
    } else {
      0
    }

  // endregion

  return layout(constraints.maxWidth, constraints.constrainHeight(contentHeight)) {

    // region // === Placement Phase ===

    val viewportRange = viewport.timeRange
    // Place all the children.
    for (placeable in placeables) {
      // Pull out the parent data (also available to placeable) and
      //   use it to calculate the x-position.
      //   Skip this placeable if for some reason a time range was not
      //   defined.
      val timeRange = placeable.timeRange ?: continue

      // Only place this child if its in the viewable range.
      if (timeRange.overlaps(viewportRange)) {
        val x = ((timeRange.first - viewport.startTime) * pxPerMinute).roundToInt()
        // Place the child.
        placeable.placeRelative(x = x, y = 0)
      }
    }

    // endregion
  }
}
