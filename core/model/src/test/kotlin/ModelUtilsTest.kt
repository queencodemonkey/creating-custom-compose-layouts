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

import org.junit.Test
import rt.yotei.model.Session
import rt.yotei.model.ext.minutesBetween
import java.time.LocalDateTime
import kotlin.test.assertEquals

/**
 * Unit tests for model utility methods.
 */
class ModelUtilsTest {
  /**
   * Tests the [minutesBetween] utility method when the session's start time is
   * greater than the reference start time.
   */
  @Test
  fun testMinutesBetween_sessionTimeGreaterThanStartTime() {
    val referenceDateTime = LocalDateTime.of(2023, 1, 1, 10, 0)
    val minutesBetween = 45L
    val session = Session(
      name = "Test Session",
      instructor = "Test Instructor",
      startTime = referenceDateTime.plusMinutes(minutesBetween),
      endTime = referenceDateTime.plusMinutes(minutesBetween + 45L),
      location = "Test Location",
      category = Session.Category.Strength,
    )
    assertEquals(expected = minutesBetween.toInt(), actual = minutesBetween(referenceDateTime, session))
  }

  /**
   * Tests the [minutesBetween] utility method when the session's start time is
   * less than the reference start time.
   */
  @Test
  fun testMinutesBetween_sessionTimeLessThanStartTime() {
    val referenceDateTime = LocalDateTime.of(2023, 1, 1, 10, 0)
    val minutesBetween = 45L
    val startTime = referenceDateTime.minusMinutes(minutesBetween)
    val session = Session(
      name = "Test Session",
      instructor = "Test Instructor",
      startTime = startTime,
      endTime = startTime.plusMinutes(45L),
      location = "Test Location",
      category = Session.Category.Strength,
    )
    assertEquals(expected = -minutesBetween.toInt(), actual = minutesBetween(referenceDateTime, session))
  }
}