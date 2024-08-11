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

package rt.yotei.schedule.ui.track

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Constraints.Companion.Infinity
import androidx.compose.ui.unit.constrainHeight
import kotlinx.collections.immutable.ImmutableList
import rt.yotei.model.Session
import rt.yotei.model.ext.toEpochMinute
import rt.yotei.schedule.ext.length
import rt.yotei.schedule.ui.ScheduleScope
import rt.yotei.schedule.ui.timeRange
import rt.yotei.schedule.ui.session.Session
import kotlin.math.roundToInt

/**
 * Single track of the schedule.
 *
 * Scoped to [rt.yotei.schedule.ui.schedule.Schedule] composable
 * via the [ScheduleScope].
 */
@Composable
fun ScheduleScope.ScheduleTrack(
  sessions: ImmutableList<Session>,
  modifier: Modifier = Modifier,
  onSessionClick: (Session) -> Unit = {},
) {
  Layout(
    modifier = modifier,
    // region // ==== Composition ====

    content = {
      // Map each session data object to a session composable.
      for (session in sessions) {
        key(session.hashCode()) {
          Session(
            session = session,
            // No more passing an entire data object as a layoutId.
            modifier = Modifier.timeRange(
              session.startTime.toEpochMinute(),
              session.endTime.toEpochMinute()
            ),
            onClick = {
              onSessionClick(session)
            },
          )
        }
      }
    }

    // endregion

  ) { measurables, constraints ->

    val pxPerMinute: Float =
      if (constraints.maxWidth == Infinity) {
        1f
      } else {
        constraints.maxWidth.toFloat() / viewport.duration
      }

    // region // ==== But what if overlap? ====

//    // Determine groups of overlapping items.
//    var currentGroup = mutableListOf<Measurable>()
//    val overlapGroups = mutableListOf<List<Measurable>>(currentGroup)
//    for (measurable in measurables) {
//      if (currentGroup.isEmpty() || currentGroup.last().timeRange.overlaps(measurable.timeRange)) {
//        currentGroup.add(measurable)
//      } else {
//        currentGroup = mutableListOf(measurable)
//        overlapGroups.add(currentGroup)
//      }
//    }
//
//    val placeableGroups: List<List<Pair<Placeable, Long>>> = overlapGroups.map { measurablesGroup ->
//      measurablesGroup.map { measurable ->
//        val startTime = measurable.timeRange.first
//        val endTime = measurable.timeRange.last
//
//        val minutes = endTime - startTime
//
//        val sessionConstraints = Constraints.fixedWidth((minutes * pxPerMinute).roundToInt())
//
//        measurable.measure(sessionConstraints) to startTime
//      }
//    }
//
//    val contentHeight = placeableGroups.maxOf { placeableGroup ->
//      placeableGroup.sumOf { (placeable, _) ->
//        placeable.height
//      }
//    }

    // endregion

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
    val contentHeight = placeables.maxOf(Placeable::height)

    // endregion

    layout(constraints.maxWidth, constraints.constrainHeight(contentHeight)) {

      // region // === Placement Phase ===

      // Place all the children.
      for (placeable in placeables) {
        // Pull out the parent data (also available to placeable) and
        //   use it to calculate the x-position.
        val x = ((placeable.timeRange.first - viewport.startTime) * pxPerMinute).roundToInt()
        // Place the child.
        placeable.placeRelative(x = x, y = 0)
      }

      // region // ==== But what if overlap? ====

//      for (placeableGroup in placeableGroups) {
//        var y = 0
//        for ((placeable, startTime) in placeableGroup) {
//          val x = ((startTime - viewport.startTime) * pxPerMinute).roundToInt()
//          placeable.placeRelative(x = x, y = y)
//          y += placeable.height
//        }
//      }

      // endregion

      // endregion
    }
  }
}
