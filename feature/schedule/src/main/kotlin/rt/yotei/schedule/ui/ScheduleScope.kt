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

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

/**
 * Receiver scope for children of [Schedule] composables.
 * Provides viewport information to children whose
 * measure/layout depends on the viewport.
 */
// region // ==== About Stability in Compose ====


// Recompositions occur when state has changed OR
//   if Compose can't know whether the state has
//   changed.


// A STABLE type is either immutable or notifies
//   Compose when its value has changed.
// An UNSTABLE type is one where Compose does not
//   know whether its value has changed.




// When a Composable has stable state that have not changed,
//   Compose will SKIP that Composable during recomposition.
//
// When a Composable has unstable parameters,
//   it always recomposes if its parent recomposes.




//
// Our schedule scope will hold lots of state that become
//   parameters in our session/event elements.
// To make skipping possible, we want to make scope stable.
//




// In general, stable types for parameters into Compose
//   functions are GOOD and help with that skipping.

// Having unstable types is not the end of the world and
//   always benchmark before worrying about optimization
//   BUT it is an important concept to beware of.

// endregion
@Stable
interface ScheduleScope {
  /**
   * The current viewport of the schedule.
   */
  var viewport: ScheduleViewport

  /**
   * The number of minutes per pixel to use
   * when drawing sessions in the Schedule.
   */
  var minutesPerPixel: Float

  /**
   * Modifier that allows a child to specify its
   * start/end time on the schedule.
   *
   * @param startTime a child's start time (minutes since the epoch).
   * @param endTime a child's end time (minutes since the epoch).
   */
  fun Modifier.timeRange(
    startTime: Long,
    endTime: Long,
  ): Modifier
}
