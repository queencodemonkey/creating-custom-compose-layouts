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

package rt.cccl.schedule.ui.track

import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Constraints.Companion.Infinity
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import rt.cccl.model.Session
import rt.cccl.model.ext.toEpochMinute
import rt.cccl.schedule.ext.overlaps
import rt.cccl.schedule.ui.schedule.ScheduleScope
import rt.cccl.schedule.ui.schedule.timeRange
import rt.cccl.schedule.ui.session.Session
import kotlin.math.roundToInt

@Composable
fun ScheduleScope.ScheduleTrack(
  sessions: ImmutableList<Session>,
  modifier: Modifier = Modifier,
  onSessionClick: (Session) -> Unit = {},
) {
  Layout(
    modifier = modifier.border(width = 3.dp, color = Color.Yellow),
    content = {
      for (session in sessions) {
        key(session.hashCode()) {
          Session(
            session = session,
//            modifier = Modifier.layoutId(session),
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
  ) { measurables, constraints ->

    val pxPerMinute: Float =
      if (constraints.maxWidth == Infinity) {
        1f
      } else {
        constraints.maxWidth.toFloat() / viewport.duration
      }
    // region // === Measurement Phase ===

//    val measuredSessions = measurables.map { it.layoutId as Session }
//    val placeables = measurables.mapIndexed { index, measurable ->
//      val session = sessions[index]
//      val minutes = session.totalMinutes
//      val width = (minutes * pxPerMinute).roundToInt()
//      val height = measurable.minIntrinsicHeight(width)
//      val sessionConstraints = Constraints.fixed(width, height)
//      measurable.measure(sessionConstraints)
//    }
//
//    val contentHeight = placeables.maxOf(Placeable::height)


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
      measurablesGroup.map { measurable ->
        val startTime = measurable.timeRange?.first ?: viewport.startTimeMinutes
        val endTime = measurable.timeRange?.last ?: viewport.endTimeMinutes

        val minutes = endTime - startTime

        val width = (minutes * pxPerMinute).roundToInt()
        val height = measurable.minIntrinsicHeight(width)
        val sessionConstraints = Constraints.fixed(width, height)

        measurable.measure(sessionConstraints) to startTime
      }
    }

    val contentHeight = placeableGroups.maxOf { placeableGroup ->
      placeableGroup.sumOf { (placeable, _) ->
        placeable.height
      }
    }

    // endregion

    // region // === Placement Phase ===
    layout(constraints.maxWidth, constraints.constrainHeight(contentHeight)) {
//      val xCoordinates: List<Int> =
//        if (measuredSessions.isEmpty()) {
//          emptyList()
//        } else {
//          measuredSessions.map { session ->
//            ((session.startTime.toEpochMinute() - viewport.startTimeMinutes) * pxPerMinute)
//              .roundToInt()
//          }
//        }
//      placeables.forEachIndexed { index, placeable ->
//        placeable.placeRelative(x = xCoordinates[index], y = 0)
//      }
//        for ((placeable, startTime) in placeablesStartTimeMap) {
//          val x = ((startTime - viewport.startTimeMinutes) * pxPerMinute).roundToInt()
//          placeable.placeRelative(x = x, y = 0)
//        }
      for (placeableGroup in placeableGroups) {
        var y = 0
        for ((placeable, startTime) in placeableGroup) {
          val x = ((startTime - viewport.startTimeMinutes) * pxPerMinute).roundToInt()
          placeable.placeRelative(x = x, y = y)
          y += placeable.height
        }
      }
    }
  }
  // endregion
}
