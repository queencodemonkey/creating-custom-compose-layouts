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

package rt.yotei.schedule.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * Create and remember the default [ScheduleScope] implementation
 * with [viewport] as the initial viewport, which will be used as
 * the source of truth for layout of children within scope.
 */
@Composable
internal fun rememberScheduleScope(
  viewport: ScheduleViewport,
): ScheduleScope = rememberSaveable(
  saver = ScheduleScopeImpl.Saver,
  init = {
    ScheduleScopeImpl(viewport = viewport)
  }
  // Taken from `rememberUpdatedState`. I wish I was this smart.
).also {scope -> scope.viewport = viewport }

/**
 * Concrete implementation of [ScheduleScope] that provides
 * [viewport] information to [Schedule] children.
 */
internal class ScheduleScopeImpl(
  viewport: ScheduleViewport,
  minutesPerPixel: Float = 0f,
) : ScheduleScope {

  override var viewport by mutableStateOf(viewport)

  override var minutesPerPixel by mutableFloatStateOf(minutesPerPixel)

  override fun Modifier.timeRange(startTime: Long, endTime: Long) =
    then(TimeRangeElement(
      startEndTimeRange = startTime..endTime,
      inspectorInfo = {
        name = "timeRange"
        properties["startTimeMinutes"] = startTime
        properties["endTimeMinutes"] = endTime
      }
    ))

  companion object {
    /**
     * Describes how a [ScheduleScopeImpl] may be saved;
     * used for `rememberSaveable`.
     */
    val Saver = listSaver(
      save = { listOf(it.viewport, it.minutesPerPixel) },
      restore = { savedValues ->
        ScheduleScopeImpl(
          viewport = savedValues[0] as ScheduleViewport,
          minutesPerPixel = savedValues[1] as Float
        )
      }
    )
  }
}