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

@file:OptIn(ExperimentalSerializationApi::class)

package rt.yotei.network.demo

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import rt.cccl.network.R
import rt.yotei.network.GymDataSource
import rt.yotei.network.model.ApiMaintenanceEvent
import rt.yotei.network.model.ApiOpenGymDutyShiftList
import rt.yotei.network.model.ApiSession
import javax.inject.Inject

/**
 * Concrete implementation of [GymDataSource] that provides static data
 * from a local JSON asset.
 */
class DemoGymDataSource
  @Inject
  constructor(
    @ApplicationContext private val context: Context,
  ) : GymDataSource {
    override suspend fun getClasses(): List<ApiSession> {
      val inputStream = context.resources.openRawResource(R.raw.fitness_classes_01_nol)
      val sessions = Json.decodeFromStream<List<ApiSession>>(inputStream)
      return sessions
    }

    override suspend fun getOpenGymDutyShifts(): List<ApiOpenGymDutyShiftList> {
      val inputStream = context.resources.openRawResource(R.raw.open_gym_duty_shifts)
      return Json.decodeFromStream<List<ApiOpenGymDutyShiftList>>(inputStream)
    }

    override suspend fun getMaintenanceEvents(): List<ApiMaintenanceEvent> {
      val inputStream = context.resources.openRawResource(R.raw.maintenance_events)
      return Json.decodeFromStream<List<ApiMaintenanceEvent>>(inputStream)
    }
  }
