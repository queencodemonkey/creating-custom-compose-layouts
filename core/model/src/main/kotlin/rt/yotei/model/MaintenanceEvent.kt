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
import rt.yotei.model.ext.toEpochMinute
import java.time.LocalDateTime

/**
 * Represents a maintenance event defined by a [type]
 * and required to start at [startTime].
 */
@Immutable
data class MaintenanceEvent(
  val type: Type,
  val startTime: LocalDateTime,
) : ScheduleItem {
  override val start = startTime.toEpochMinute()
  override val end = start
  override val duration = 0L
  override val range = start..end

  /**
   * Represents the type of maintenance event.
   */
  enum class Type(val displayName: String) {
    WaterDelivery("Water Delivery"),
    EquipmentTidy("Equipment Tidy"),
    TowelSwap("Towel Swap");

    companion object {
      /**
       * Finds a [MaintenanceEvent] by its display name.
       *
       * @param displayName The display name of the event to search for.
       * @return The [MaintenanceEvent] with the given display name, or
       * `null` if no such event exists.
       */
      fun fromDisplayName(displayName: String) = entries.find { it.displayName == displayName }
    }
  }
}
