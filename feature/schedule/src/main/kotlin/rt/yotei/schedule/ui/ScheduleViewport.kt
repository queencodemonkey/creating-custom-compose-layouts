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

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToLong

/**
 * Viewable window of a [Schedule] with a minimum time of [minTime],
 * a maximum time of [maxTime], and a currently viewable range of
 * [startTime]-[endTime], inclusive and constrained by
 * the maximum and minimum. All times are in minutes.
 */
@Parcelize
@Immutable
data class ScheduleViewport(
  val startTime: Long,
  val endTime: Long,
  val minTime: Long,
  val maxTime: Long,
) : Parcelable {

  init {
    // Check that start and end times are within bounds
    // and that start < end (the time range is not backwards).
    // Throw an error if invalid.
    validate()
  }

  /**
   * Maximum possible duration of this viewport, defined by
   * [minTime] and [maxTime].
   */
  val maxDuration: Long
    get() = maxTime - minTime

  /**
   * Currently viewable range in minutes: [start, end].
   */
  val timeRange: LongRange
    get() = startTime..endTime

  /**
   * Duration of the currently viewable range in minutes.
   */
  val duration: Long
    get() = endTime - startTime

  /**
   * Current viewport duration as a percentage of the maximum possible duration.
   */
  val maxDurationPercent: Float
    get() = duration.toFloat() / maxDuration

  /**
   * Returns a new viewport that is the current viewport shifted by [deltaMinutes].
   */
  infix fun shiftBy(deltaMinutes: Long): ScheduleViewport {
    // Shift the start time, keep the duration of the viewport,
    //   but ensure it stays within the valid range.
    val newStart =
      (startTime + deltaMinutes)
        .coerceIn(minTime, maxTime - duration)
    // Re-calculate the end based on the start, keeping the same duration.
    val newEnd = newStart + duration
    // Maintain the same min/max times as this viewport, but
    //   update the start/end times.
    return copy(
      startTime = newStart,
      endTime = newEnd,
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
      startTime + (endTime - startTime) * 0.5f
    // Keeping the same middle of the view port but changing the duration,
    // calculate the new start of the viewport.
    val newStartTimeMinutes = (currentCenterTimeMinutes - (newDuration * 0.5f)).roundToLong()
      .coerceAtLeast(minTime)
    val newEndTimeMs = newStartTimeMinutes + newDuration
    return if (newEndTimeMs > maxTime) {
      // If the new end is past the max date/time, then we shift
      // the viewport back so that the end is at the max date/time.
      copy(
        startTime = maxTime - newDuration,
        endTime = maxTime
      )
    } else {
      // Otherwise, the new viewport is valid and instantiate it.
      copy(
        startTime = newStartTimeMinutes,
        endTime = newEndTimeMs
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
    if (startTime > endTime) {
      error("Invalid ${this::class.simpleName}: start date/time > end date/time.")
    }

    if (startTime < minTime || endTime > maxTime) {
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
          startTime = minTimeMinutes,
          endTime = minTimeMinutes + DEFAULT_VIEWPORT_DURATION_MS,
          minTime = minTimeMinutes,
          maxTime = maxTimeMinutes,
        )
      } else {
        ScheduleViewport(
          startTime = minTimeMinutes,
          endTime = maxTimeMinutes,
          minTime = minTimeMinutes,
          maxTime = maxTimeMinutes,
        )
      }
    }
  }
}