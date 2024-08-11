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
import kotlinx.coroutines.flow.flow
import rt.yotei.data.ext.toMaintenanceEvent
import rt.yotei.data.ext.toOpenGymDutyShiftList
import rt.yotei.data.ext.toSession
import rt.yotei.model.MaintenanceEvent
import rt.yotei.model.OpenGymDutyShiftList
import rt.yotei.model.Session
import rt.yotei.network.GymDataSource
import rt.yotei.network.model.ApiMaintenanceEvent
import rt.yotei.network.model.ApiOpenGymDutyShiftList
import rt.yotei.network.model.ApiSession
import javax.inject.Inject

/**
 * Concrete implementation of [GymDataRepository].
 */
class DemoGymDataRepository
@Inject
constructor(
  private val gymDataSource: GymDataSource,
) : GymDataRepository {
  override fun getSessions(): Flow<List<Session>> =
    flow {
      emit(gymDataSource.getClasses()
        .map(ApiSession::toSession)
        .sortedBy(Session::startTime)
      )
    }

  override fun getOpenGymDutyShifts(): Flow<List<OpenGymDutyShiftList>> =
    flow {
      emit(gymDataSource.getOpenGymDutyShifts()
        .map(ApiOpenGymDutyShiftList::toOpenGymDutyShiftList)
        .sortedBy(OpenGymDutyShiftList::date)
      )
    }

  override fun getMaintenanceEvents(): Flow<List<MaintenanceEvent>> =
    flow {
      emit(gymDataSource.getMaintenanceEvents()
        .map(ApiMaintenanceEvent::toMaintenanceEvent)
        .sortedBy(MaintenanceEvent::startTime)
      )
    }
}
