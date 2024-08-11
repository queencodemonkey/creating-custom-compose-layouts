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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
internal fun rememberScheduleScope(
  viewport: ScheduleViewport,
): ScheduleScope = rememberSaveable(
  saver = ScheduleScopeImpl.Saver
) {
  ScheduleScopeImpl(viewport = viewport)
}

/**
 * Concrete implementation of [ScheduleScope] that provides
 * [viewport] information to [Schedule] children.
 */
internal class ScheduleScopeImpl(
  viewport: ScheduleViewport,
) : ScheduleScope {

  private var _viewport by mutableStateOf(viewport)
  override var viewport: ScheduleViewport
    get() = _viewport
    set(value) {
      _viewport = value
    }

  override fun Modifier.timeRange(startTimeMinutes: Long, endTimeMinutes: Long) =
    then(ScheduleTimeRangeElement(
      startEndTimeRange = startTimeMinutes..endTimeMinutes,
      inspectorInfo = {
        name = "timeRange"
        properties["startTimeMinutes"] = startTimeMinutes
        properties["endTimeMinutes"] = endTimeMinutes
      }
    ))

  companion object {
    val Saver = Saver<ScheduleScopeImpl, ScheduleViewport>(
      save = { it.viewport },
      restore = { ScheduleScopeImpl(it) }
    )
  }
}