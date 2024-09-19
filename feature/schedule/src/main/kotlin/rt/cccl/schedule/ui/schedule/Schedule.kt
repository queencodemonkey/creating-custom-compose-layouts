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

package rt.cccl.schedule.ui.schedule

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import rt.cccl.model.Session
import rt.cccl.model.ext.toEpochMinute
import rt.cccl.schedule.ext.maxDateTime
import rt.cccl.schedule.ext.minDateTime
import rt.cccl.schedule.ui.common.scheduleTrackStyle
import rt.cccl.schedule.ui.track.ScheduleTrack
import kotlin.math.abs
import kotlin.math.roundToLong


/**
 * Displays the [sessionsByLocation] and their position in time
 * as a schedule.
 */
@Composable
fun Schedule(
  sessionsByLocation: ImmutableMap<String, ImmutableList<Session>>,
  onSessionClick: (Session) -> Unit,
  modifier: Modifier = Modifier,
) {
  // Viewable segment of the schedule.
  val scope = rememberScheduleScope(
    ScheduleViewport.defaultScheduleViewport(
      minTimeMinutes = sessionsByLocation.minDateTime.toEpochMinute(),
      maxTimeMinutes = sessionsByLocation.maxDateTime.toEpochMinute(),
    )
  )

  // Accumulator for scroll deltas. We need to wait until we have at least
  // .5 pixel of scroll to update the rendering because the delta can be
  // quite tiny and update frequently. This is a simplified version of
  // `LazyList` logic.
  var scrollToConsume by remember { mutableFloatStateOf(0f) }

  // Going to replace the DPs/minute with this for now.
  var minutesPerPixel by remember { mutableFloatStateOf(0f) }

  Layout(
    modifier = modifier
      .verticalScroll(rememberScrollState())
      // region // ==== Pay no attention to that modifier behind the curtain! ====

      .transformable(
        rememberTransformableState { zoomChange, panChange, _ ->
          scope.viewport *= 1f / zoomChange

          val shift = -(panChange.x * minutesPerPixel).roundToLong()
          scope.viewport = scope.viewport shiftBy shift
        }
      ),

      // endregion

      // region // ==== Making the Viewport Scrollable ====

//      .scrollable(
//        orientation = Orientation.Horizontal,
//        state = rememberScrollableState { delta ->
//          // Accumulate new delta from scroll
//          scrollToConsume += delta
//          // If this accumulates 0.5 pixels of scroll,
//          // send out the callback to update the viewport and
//          // reset the accumulator
//          if (abs(scrollToConsume) >= 0.5f) {
//            val shift = -(scrollToConsume * minutesPerPixel).roundToLong()
//            scope.viewport = scope.viewport shiftBy shift
//            scrollToConsume = 0f
//          }
//          // Required return value of consumed scroll.
//          delta
//        }
//      ),

    // endregion

    content = {
      for (location in sessionsByLocation.keys) {
        // Retrieve all the sessions for a given location.
        val locationSessions = sessionsByLocation[location] ?: persistentListOf()

        // Row-based track Composable
        scope.ScheduleTrack(
          modifier = Modifier
            .fillMaxWidth()
            .scheduleTrackStyle(),
          sessions = locationSessions,
          onSessionClick = onSessionClick,
        )
      }
    },
    measurePolicy = { measurables, constraints ->
      minutesPerPixel = scope.viewport.duration.toFloat() / constraints.maxWidth.toFloat()

      // Just use parameter constraints to measure all the measurables.
      val contentConstraints = Constraints.fixedWidth(constraints.maxWidth)
      val placeables = measurables.map { it.measure(contentConstraints) }
      val height = placeables.sumOf(Placeable::height)
      layout(constraints.maxWidth, height) {
        // Basically, position all the placeables like a column.
        var y = 0
        for (placeable in placeables) {
          placeable.place(x = 0, y = y)
          y += placeable.height
        }
      }
    }
  )
}
