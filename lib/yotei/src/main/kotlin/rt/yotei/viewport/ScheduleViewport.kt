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

@file:Suppress("PROPERTY_WONT_BE_SERIALIZED")

package rt.yotei.viewport

import kotlinx.parcelize.Parcelize
import kotlin.math.roundToLong

@Parcelize
data class ScheduleViewport(
  override val min: Long,
  override val max: Long,
  override val start: Long,
  override val end: Long,
) : Viewport<Long, Long> {

  override val maxDuration = max - min
  override val duration = end - start
  override val maxRange = min..max
  override val range = start..end
  override val maxDurationPercent = duration.toFloat() / maxDuration

  override fun shiftBy(delta: Long): ScheduleViewport {
    // Shift the start time, keep the duration of the viewport,
    //   but ensure it stays within the valid range.
    val newStart = (start + delta).coerceIn(min, max - duration)
    // Re-calculate the end based on the start, keeping the same duration.
    val newEnd = newStart + duration
    // Maintain the same min/max times as this viewport, but
    //   update the start/end times.
    return copy(
      start = newStart,
      end = newEnd,
    )
  }

  override fun scaleBy(scaleFactor: Float): ScheduleViewport {
    // Scale the duration by the scale factor.
    val newDuration = (duration * scaleFactor).roundToLong()
      .coerceAtMost(maxDuration)
    // Find the current middle of the viewport.
    val currentCenter = start + (end - start) * 0.5f
    // Keeping the same middle of the view port but changing the duration,
    // calculate the new start of the viewport.
    val mewStart =
      (currentCenter - (newDuration * 0.5f)).roundToLong().coerceAtLeast(min)
    val newEnd = mewStart + newDuration
    return if (newEnd > max) {
      // If the new end is past the max date/time, then we shift
      // the viewport back so that the end is at the max date/time.
      copy(
        start = max - newDuration,
        end = max
      )
    } else {
      // Otherwise, the new viewport is valid and instantiate it.
      copy(
        start = mewStart,
        end = newEnd
      )
    }
  }

  override fun times(x: Float): ScheduleViewport = scaleBy(x)

  override fun validate() {
    if (start > end) {
      error("Invalid ${this::class.simpleName}: start > end.")
    }

    if (start < min) {
      error("Invalid ${this::class.simpleName}: start out of bounds.")
    }

    if (end > max) {
      error("Invalid ${this::class.simpleName}: end out of bounds.")
    }
  }

  companion object {
    private const val MINUTES_PER_HOUR = 60
    private const val DEFAULT_VIEWPORT_DURATION_MS = MINUTES_PER_HOUR * 3

    val Factory = object : Viewport.Factory<Long, Long, ScheduleViewport> {
      override fun defaultViewport(min: Long, max: Long): ScheduleViewport {
          val duration = max - min
          return if (duration > DEFAULT_VIEWPORT_DURATION_MS) {
            ScheduleViewport(
              start = min,
              end = min + DEFAULT_VIEWPORT_DURATION_MS,
              min = min,
              max = max,
            )
          } else {
            ScheduleViewport(
              start = min,
              end = max,
              min = min,
              max = max,
            )
          }
      }
    }
  }
}