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

package rt.yotei.schedule

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import rt.yotei.viewport.ScheduleViewport
import kotlin.math.abs
import kotlin.math.roundToLong

@Composable
fun ScheduleLayout(
  viewport: ScheduleViewport,
  modifier: Modifier = Modifier,
  onViewportChange: (ScheduleViewport) -> Unit = {},
  measurePolicy: MeasurePolicy = DefaultMeasurePolicy,
  content: @Composable ScheduleScope.() -> Unit = {},
) {
  val scope = rememberScheduleScope(viewport = viewport)
  val screenWidthPx = with(LocalDensity.current) {
    LocalConfiguration.current.screenWidthDp.toDp().roundToPx()
  }
  val scheduleMeasurePolicy = remember {
    ScheduleMeasurePolicy(
      measurePolicy = measurePolicy,
      scope = scope,
      screenWidthPx = screenWidthPx,
    )
  }

  Layout(
    modifier = modifier
      .scrollable(
        orientation = Orientation.Horizontal,
        state = rememberScheduleScrollableState { scrollToConsume ->
          val shift = -(scrollToConsume * scope.minutesPerPixel).roundToLong()
          scope.viewport = scope.viewport shiftBy shift
          onViewportChange(scope.viewport)
        }
      )
      .verticalScroll(rememberScrollState()),
    content = { scope.content() },
    measurePolicy = scheduleMeasurePolicy,
  )
}

@Composable
internal fun rememberScheduleScrollableState(
  onConsumeScroll: (Float) -> Unit,
): ScrollableState {
  var scrollToConsume by remember { mutableFloatStateOf(0f) }
  return rememberScrollableState { delta ->
    // Accumulate new delta from scroll
    scrollToConsume += delta
    // If this accumulates 0.5 pixels of scroll,
    // send out the callback to update the viewport and
    // reset the accumulator
    if (abs(scrollToConsume) >= 0.5f) {
      onConsumeScroll.invoke(scrollToConsume)
      scrollToConsume = 0f
    }
    // Required return value of consumed scroll.
    delta
  }
}

private class ScheduleMeasurePolicy(
  private val scope: ScheduleScope,
  private val measurePolicy: MeasurePolicy,
  private val screenWidthPx: Int,
) : MeasurePolicy by measurePolicy {
  override fun MeasureScope.measure(
    measurables: List<Measurable>,
    constraints: Constraints,
  ): MeasureResult {
    scope.minutesPerPixel = when (constraints.maxWidth) {
      0 -> 0f
      Constraints.Infinity -> scope.viewport.duration.toFloat() / screenWidthPx
      else -> scope.viewport.duration.toFloat() / constraints.maxWidth.toFloat()
    }
    return with(measurePolicy) {
      measure(measurables, constraints)
    }
  }
}

private val DefaultMeasurePolicy: MeasurePolicy =
  MeasurePolicy { measurables, constraints ->
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

