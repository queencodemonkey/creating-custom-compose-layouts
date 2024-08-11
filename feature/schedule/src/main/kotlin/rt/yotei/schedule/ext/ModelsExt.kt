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

package rt.yotei.schedule.ext

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import rt.yotei.model.Session
import rt.yotei.model.ext.toEpochMinute
import java.time.LocalDateTime

val Session.startTimeMinutes: Long
  get() = startTime.toEpochMinute()

val Session.endTimeMinutes: Long
  get() = endTime.toEpochMinute()

val List<Session>.startTime: LocalDateTime
  get() = minOf(Session::startTime)

val List<Session>.endTime: LocalDateTime
  get() = maxOf(Session::endTime)

val ImmutableMap<String, ImmutableList<Session>>.minDateTime: LocalDateTime
  get() = flatMap(Map.Entry<String, ImmutableList<Session>>::value)
    .minOf(Session::startTime)
val ImmutableMap<String, ImmutableList<Session>>.maxDateTime: LocalDateTime
  get() = flatMap(Map.Entry<String, ImmutableList<Session>>::value)
    .maxOf(Session::endTime)
