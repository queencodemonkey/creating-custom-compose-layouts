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

package rt.yotei.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import rt.yotei.model.ext.toEpochMinute
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Represents a list of [shifts] of employees covering the open gym
 * for a given [date].
 */
@Immutable
data class OpenGymDutyShiftList(
  val date: LocalDate,
  val shifts: ImmutableList<OpenGymDutyShift>,
)

/**
 * Represents a single shift to cover the open gym by employee
 * with [name], with the shift starting at [startTime] and
 * lasting for [duration].
 */
@Immutable
data class OpenGymDutyShift(
  val name: String,
  val startTime: LocalDateTime,
  override val duration: Long,
) : ScheduleItem {
  override val start = startTime.toEpochMinute()
  override val end = start + duration
  override val range = start..end
}
