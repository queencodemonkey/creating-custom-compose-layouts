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

package rt.yotei.viewport

import android.os.Parcelable
import androidx.compose.runtime.Immutable

/**
 * Viewable window defined along one axis by a [start] and an [end],
 * values of both are constrained by a [min] and a [max].
 *
 * A viewport also contains a [min] and [max] that defines
 * the valid range of values for [start] and [end] for this
 * viewport and any viewports derived from it.
 */
@Immutable
interface Viewport<T : Comparable<T>, D> : Parcelable {
  val min: T
  val max: T
  val start: T
  val end: T

  /**
   * Maximum possible duration of this viewport, defined by
   * [min] and [max].
   */
  val maxDuration: D

  /**
   * Duration of the currently viewable range, defined by
   * [start] and [end].
   */
  val duration: D

  /**
   * Maximum possible viewable range: [min, max].
   */
  val maxRange: ClosedRange<T>

  /**
   * Currently viewable range: [start, end].
   */
  val range: ClosedRange<T>

  /**
   * Current viewport duration as a percentage of the maximum possible duration.
   */
  val maxDurationPercent: Float

  /**
   * Returns a new viewport that is the current viewport
   * shifted by [delta].
   */
  infix fun shiftBy(delta: D): Viewport<T, D>

  /**
   * Return a new viewport that is the current viewport
   * scaled by [scaleFactor]. The scaling is centered on
   * the midpoint of current viewport. The duration is
   * scaled and then the viewport re-centered on the
   * existing midpoint.
   */
  infix fun scaleBy(scaleFactor: Float): Viewport<T, D>


  /**
   * Return a new viewport that is the current viewport
   * scaled by [x].
   */
  operator fun times(x: Float): Viewport<T, D>

  /**
   * Validates this [ScheduleViewport] and throws errors if
   * the range of this viewport is invalid.
   *
   * @throws IllegalStateException
   */
  fun validate()

  /**
   * Factory for creating [Viewport] instances.
   */
  interface Factory<T : Comparable<T>, S, U : Viewport<T, S>> {
    /**
     * Returns a default viewport give a [min] and [max].
     * The default viewport is a heuristic that shows the
     * entire range if the range of the viewport defined
     * by [min] and [max] is short and a default range
     * if that range is long.
     */
    fun defaultViewport(min: T, max: T): U
  }
}