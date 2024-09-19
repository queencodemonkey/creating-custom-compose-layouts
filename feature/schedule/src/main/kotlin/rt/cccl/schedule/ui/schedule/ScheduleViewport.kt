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

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToLong

/**
 * Viewable window of a [Schedule] with a minimum time of [minTimeMinutes],
 * a maximum time of [maxTimeMinutes], and a currently viewable range of
 * [startTimeMinutes]-[endTimeMinutes], inclusive and constrained by
 * the maximum and minimum.
 */
@Parcelize
@Immutable
data class ScheduleViewport(
  val startTimeMinutes: Long,
  val endTimeMinutes: Long,
  val minTimeMinutes: Long,
  val maxTimeMinutes: Long,
) : Parcelable {

  init {
    // Check that start and end times are within bounds
    // and that start < end (the time range is not backwards).
    // Throw an error if invalid.
    validate()
  }

  /**
   * Duration of the entire valid time range for this viewport in minutes.
   */
  private val maxDuration: Long
    get() = maxTimeMinutes - minTimeMinutes

  /**
   * Currently viewable range in minutes: [start, end].
   */
  val timeRange: LongRange
    get() = startTimeMinutes..endTimeMinutes

  /**
   * Duration of the currently viewable range in minutes.
   */
  val duration: Long
    get() = endTimeMinutes - startTimeMinutes

  /**
   * Returns a new viewport that is the current viewport shifted by [deltaMinutes].
   */
  infix fun shiftBy(deltaMinutes: Long): ScheduleViewport {
    // Shift the start time, keep the duration of the viewport,
    //   but ensure it stays within the valid range.
    val newStart =
      (startTimeMinutes + deltaMinutes)
        .coerceIn(minTimeMinutes, maxTimeMinutes - duration)
    // Re-calculate the end based on the start, keeping the same duration.
    val newEnd = newStart + duration
    // Maintain the same min/max times as this viewport, but
    //   update the start/end times.
    return copy(
      startTimeMinutes = newStart,
      endTimeMinutes = newEnd,
    )
  }

  /**
   * Return a new viewport that is the current viewport scaled by [scaleFactor].
   * The scaling is centered on the midpoint of current viewport.
   * The duration is scaled and then the viewport re-centered on
   * the existing midpoint.
   */
  private fun scaleBy(scaleFactor: Float): ScheduleViewport {
    // Scale the duration by the scale factor.
    val newDuration = (duration * scaleFactor).roundToLong()
      .coerceAtMost(maxDuration)
    // Find the current middle of the viewport.
    val currentCenterTimeMinutes =
      startTimeMinutes + (endTimeMinutes - startTimeMinutes) * 0.5f
    // Keeping the same middle of the view port but changing the duration,
    // calculate the new start of the viewport.
    val newStartTimeMinutes = (currentCenterTimeMinutes - (newDuration * 0.5f)).roundToLong()
      .coerceAtLeast(minTimeMinutes)
    val newEndTimeMs = newStartTimeMinutes + newDuration
    return if (newEndTimeMs > maxTimeMinutes) {
      // If the new end is past the max date/time, then we shift
      // the viewport back so that the end is at the max date/time.
      copy(
        startTimeMinutes = maxTimeMinutes - newDuration,
        endTimeMinutes = maxTimeMinutes
      )
    } else {
      // Otherwise, the new viewport is valid and instantiate it.
      copy(
        startTimeMinutes = newStartTimeMinutes,
        endTimeMinutes = newEndTimeMs
      )
    }
  }

  /**
   * Return a new viewport that is the current viewport scaled by [x].
   */
  operator fun times(x: Float) = scaleBy(x)

  /**
   * Validates this [ScheduleViewport] and throws errors if the
   * range of this viewport is invalid.
   */
  private fun validate() {
    if (startTimeMinutes > endTimeMinutes) {
      error("Invalid ${this::class.simpleName}: start date/time > end date/time.")
    }

    if (startTimeMinutes < minTimeMinutes || endTimeMinutes > maxTimeMinutes) {
      error("Invalid ${this::class.simpleName}: date/time out of bounds.")
    }
  }

  companion object {
    private const val MINUTES_PER_HOUR = 60
    private const val DEFAULT_VIEWPORT_DURATION_MS = MINUTES_PER_HOUR * 3

    /**
     * Returns a default viewport give a [minTimeMinutes]/[maxTimeMinutes].
     * The default viewport is a heuristic that shows the entire range if
     * the duration is short, and a default amount of the time range if the
     * duration is long.
     */
    fun defaultScheduleViewport(
      minTimeMinutes: Long,
      maxTimeMinutes: Long,
    ): ScheduleViewport {
      val duration = maxTimeMinutes - minTimeMinutes
      return if (duration > DEFAULT_VIEWPORT_DURATION_MS) {
        ScheduleViewport(
          startTimeMinutes = minTimeMinutes,
          endTimeMinutes = minTimeMinutes + DEFAULT_VIEWPORT_DURATION_MS,
          minTimeMinutes = minTimeMinutes,
          maxTimeMinutes = maxTimeMinutes,
        )
      } else {
        ScheduleViewport(
          startTimeMinutes = minTimeMinutes,
          endTimeMinutes = maxTimeMinutes,
          minTimeMinutes = minTimeMinutes,
          maxTimeMinutes = maxTimeMinutes,
        )
      }
    }
  }
}