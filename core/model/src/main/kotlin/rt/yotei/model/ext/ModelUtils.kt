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

package rt.yotei.model.ext

import rt.yotei.model.Session
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

//
// This file contains non-extension, utility methods for
// business logic related to models.
//

/**
 * Returns the number of minutes from the given [startTime] to the start time of the [session].
 * If [startTime] < [session]'s startTime, the result will be positive.
 * If [startTime > [session]'s startTime, the result will be negative.
 */
fun minutesBetween(startTime: LocalDateTime, session: Session) =
  ChronoUnit.MINUTES.between(startTime, session.startTime).toInt()

/**
 * Returns the total duration of all sessions in this list in minutes including
 * gaps between sessions.
 */
val List<Session>.overallDuration: Long
  get() = maxOf(Session::endTime).toEpochMinute() - minOf(Session::startTime).toEpochMinute()