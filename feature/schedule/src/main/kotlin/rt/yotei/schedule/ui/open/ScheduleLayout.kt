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

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
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
import rt.yotei.schedule.ui.ScheduleScope
import rt.yotei.schedule.ui.ScheduleViewport
import rt.yotei.schedule.ui.rememberScheduleScope
import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * Displays a collection of events arranged chronologically
 * in a schedule in one or more horizontally oriented tracks.
 */
@Composable
fun ScheduleLayout(
  viewport: ScheduleViewport,
  onViewportChange: (ScheduleViewport) -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable ScheduleScope.() -> Unit,
) {
  // Viewable segment of the schedule.
  val scope = rememberScheduleScope(viewport)
  var scrollToConsume by remember { mutableFloatStateOf(0f) }
  Layout(
    modifier = modifier
      .scrollable(
        orientation = Orientation.Horizontal,
        state = rememberScrollableState { delta ->
          // Accumulate new delta from scroll
          scrollToConsume += delta
          // If this accumulates 0.5 pixels of scroll,
          // send out the callback to update the viewport and
          // reset the accumulator
          if (abs(scrollToConsume) >= 0.5f) {
            val shift = -(scrollToConsume * scope.minutesPerPixel).roundToLong()
            scope.viewport = scope.viewport shiftBy shift
            onViewportChange(scope.viewport)
            scrollToConsume = 0f
          }
          // Required return value of consumed scroll.
          delta
        }
      )
      .verticalScroll(rememberScrollState()),
    content = { scope.content() },
    measurePolicy = { measurables, constraints ->
      scope.minutesPerPixel = scope.viewport.duration.toFloat() / constraints.maxWidth.toFloat()

      val contentConstraints = Constraints.fixedWidth(constraints.maxWidth)
      val placeables = measurables.map { it.measure(contentConstraints) }
      val height = placeables.sumOf(Placeable::height)
      layout(constraints.maxWidth, height) {
        var y = 0
        for (placeable in placeables) {
          placeable.place(x = 0, y = y)
          y += placeable.height
        }
      }
    }
  )
}