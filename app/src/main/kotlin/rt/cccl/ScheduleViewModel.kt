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

package rt.cccl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import rt.yotei.data.repository.GymDataRepository
import rt.yotei.model.MaintenanceEvent
import rt.yotei.model.OpenGymDutyShiftList
import rt.yotei.model.Session
import javax.inject.Inject

/**
 * View model for the schedule screens.
 */
@HiltViewModel
class ScheduleViewModel
@Inject
constructor(gymDataRepository: GymDataRepository) : ViewModel() {

  val sessions: StateFlow<ImmutableList<Session>> =
    gymDataRepository.getSessions()
      .map(List<Session>::toImmutableList)
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = persistentListOf()
      )

  val sessionsByLocation: StateFlow<ImmutableMap<String, ImmutableList<Session>>> =
    gymDataRepository.getSessions().map { sessions ->
      sessions.groupBy(Session::location)
        .mapValues { (_, sessions) -> sessions.toImmutableList() }
        .toImmutableMap()
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(),
      initialValue = persistentMapOf()
    )

  val openGymDutyShifts: StateFlow<ImmutableList<OpenGymDutyShiftList>> =
    gymDataRepository.getOpenGymDutyShifts()
      .map(List<OpenGymDutyShiftList>::toImmutableList)
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = persistentListOf()
      )

  val maintenanceEvents: StateFlow<ImmutableList<MaintenanceEvent>> =
    gymDataRepository.getMaintenanceEvents()
      .map(List<MaintenanceEvent>::toImmutableList)
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = persistentListOf()
      )
}
