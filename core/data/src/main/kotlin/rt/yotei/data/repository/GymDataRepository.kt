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

package rt.yotei.data.repository

import kotlinx.coroutines.flow.Flow
import rt.yotei.model.MaintenanceEvent
import rt.yotei.model.OpenGymDutyShiftList
import rt.yotei.model.Session

/**
 * Data layer interface for retrieving gym data using
 * the repository pattern.
 */
interface GymDataRepository {
  /**
   * Retrieves a list of all available sessions.
   */
  fun getSessions(): Flow<List<Session>>

  /**
   * Retrieves a list of all shifts for open gym duty.
   */
  fun getOpenGymDutyShifts(): Flow<List<OpenGymDutyShiftList>>

  /**
   * Retrieves a list of all maintenance events.
   */
  fun getMaintenanceEvents(): Flow<List<MaintenanceEvent>>
}
