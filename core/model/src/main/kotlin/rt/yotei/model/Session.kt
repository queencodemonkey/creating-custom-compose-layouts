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
import java.time.temporal.ChronoUnit

/**
 * Represents a scheduled session class with a [name] and the
 * [instructor] leading the class, a [startTime] and [endTime]
 * for the class, a [location], and a [category] of class.
 */
@Immutable
data class Session(
  val name: String,
  val instructor: String,
  val startTime: LocalDateTime,
  val endTime: LocalDateTime,
  val location: String,
  val category: Category,
) : ScheduleItem {

  override val start = startTime.toEpochMinute()
  override val end = endTime.toEpochMinute()
  override val duration = ChronoUnit.MINUTES.between(startTime, endTime)
  override val range: LongRange = start..end

  /**
   * Represents the different categories of sessions.
   *
   * @property displayName The display name of the category.
   */
  enum class Category(
    val displayName: String,
  ) {
    /**
     * Encompasses high-energy classes aimed at improving cardiovascular fitness,
     * like HIIT Cardio, Spin Class, Kickboxing, and Rowing.
     */
    Cardio("Cardio"),

    /**
     * Includes dance-based fitness classes like Cardio Hip Hop, Zumba, and Belly Dancing.
     */
    Dance("Dance"),

    /**
     * Includes classes focused on flexibility, balance, and mental well-being such as
     * Yoga, Pilates, Meditation, and Tai Chi.
     */
    MindBody("Mind & Body"),

    /**
     * Covers classes that focus on building muscle strength and endurance, including Strength Training,
     * TRX Training, CrossFit, and Circuit Training.
     */
    Strength("Strength"),

    /**
     * Encompasses martial arts and self-defense classes.
     */
    MartialArts("Martial Arts")
    ;

    companion object {
      /**
       * Finds a [Category] by its display name.
       *
       * @param displayName The display name of the category to search for.
       * @return The [Category] with the given display name, or `null` if no such category exists.
       */
      fun fromDisplayName(displayName: String): Category? =
        entries.find { it.displayName == displayName }
    }
  }
}

