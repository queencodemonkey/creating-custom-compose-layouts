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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Density
import kotlinx.parcelize.Parcelize
import rt.yotei.viewport.ScheduleViewport

/**
 * Receiver scope for children of schedule composables.
 * Provides viewport information to children whose
 * measure/layout depends on the viewport.
 */
@Stable
interface ScheduleScope {
  /**
   * The current viewport of the schedule.
   */
  var viewport: ScheduleViewport

  var minutesPerPixel: Float
}

/**
 * Create and remember the default [ScheduleScope] implementation
 * with [viewport] as the initial viewport, which will be used as
 * the source of truth for layout of children within scope.
 */
@Composable
internal fun rememberScheduleScope(
  viewport: ScheduleViewport,
): ScheduleScope = rememberSaveable(
  saver = ScheduleScopeImpl.Saver
) {
  ScheduleScopeImpl(viewport = viewport)
}.also { scope -> scope.viewport = viewport }

/**
 * Concrete implementation of [ScheduleScope] that provides
 * [viewport] information.
 */
internal class ScheduleScopeImpl(
  viewport: ScheduleViewport,
  minutesPerPixel: Float = 0f,
) : ScheduleScope {

  override var viewport by mutableStateOf(viewport)

  override var minutesPerPixel by mutableFloatStateOf(minutesPerPixel)

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