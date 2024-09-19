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

package rt.yotei.data.ext

import kotlinx.collections.immutable.toImmutableList
import rt.yotei.model.MaintenanceEvent
import rt.yotei.model.OpenGymDutyShift
import rt.yotei.model.OpenGymDutyShiftList
import rt.yotei.model.Session
import rt.yotei.network.model.ApiMaintenanceEvent
import rt.yotei.network.model.ApiOpenGymDutyShift
import rt.yotei.network.model.ApiOpenGymDutyShiftList
import rt.yotei.network.model.ApiSession
import java.time.LocalDateTime

//
// This file contains extension methods for model
// classes and in particular conversion methods
// for converting between network layer models,
// data layer models, and UI layer models.
//

/**
 * Converts this [ApiSession] to a UI model/[Session].
 *
 * Throws an [IllegalArgumentException] if the category of this [ApiSession]
 * does not match any of the [Session.Category].
 */
fun ApiSession.toSession() = Session(
  name = name,
  instructor = instructor,
  startTime = LocalDateTime.parse(startTime),
  endTime = LocalDateTime.parse(endTime),
  location = location,
  category = requireNotNull(Session.Category.fromDisplayName(category)) {
    "Unhandled API category: $category"
  },
)

/**
 * Converts this [ApiOpenGymDutyShiftList] to a UI model/[OpenGymDutyShiftList].
 */
fun ApiOpenGymDutyShiftList.toOpenGymDutyShiftList() = OpenGymDutyShiftList(
  date = date,
  shifts = shifts
    .map(ApiOpenGymDutyShift::toOpenGymDutyShift)
    .sortedBy(OpenGymDutyShift::start)
    .toImmutableList(),
)

/**
 * Converts this [ApiOpenGymDutyShift] to a UI model/[OpenGymDutyShift].
 */
fun ApiOpenGymDutyShift.toOpenGymDutyShift() = OpenGymDutyShift(
  name = name,
  startTime = startTime,
  duration = duration,
)

/**
 * Converts this [ApiMaintenanceEvent] to a UI model/[MaintenanceEvent].
 *
 * Throws an [IllegalArgumentException] if the type of this [ApiMaintenanceEvent]
 * does not match any of the [MaintenanceEvent.Type] entries.
 */
fun ApiMaintenanceEvent.toMaintenanceEvent() = MaintenanceEvent(
  type = requireNotNull(MaintenanceEvent.Type.fromDisplayName(type)) {
    "Unhandled API maintenance event type: $type"
  },
  startTime = LocalDateTime.parse(startTime),
)