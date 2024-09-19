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

package rt.cccl.schedule.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import rt.cccl.model.Session
import rt.cccl.model.ext.toEpochMinute
import rt.cccl.schedule.ext.maxDateTime
import rt.cccl.schedule.ext.minDateTime
import rt.cccl.schedule.ui.common.scheduleTrackStyle
import rt.cccl.schedule.ui.schedule.ScheduleLayout
import rt.cccl.schedule.ui.schedule.ScheduleViewport
import rt.cccl.schedule.ui.session.Session
import rt.cccl.schedule.ui.session.SessionDetailsPopup
import rt.cccl.schedule.ui.track.StaggeredTrackLayout
import rt.cccl.schedule.ui.track.sessions

@Composable
fun ScheduleLayoutScreen(
  sessionsByLocation: ImmutableMap<String, ImmutableList<Session>>,
  onBack: (() -> Unit),
  modifier: Modifier = Modifier,
) {

  // Handle back gesture/button press.
  BackHandler { onBack() }

  if (sessionsByLocation.isNotEmpty()) {
    // The session selected to display details in a pop-up.
    var shownSession by remember { mutableStateOf<Session?>(null) }

    Column(modifier = modifier) {
      ScheduleLayout(
        modifier = Modifier.fillMaxSize(),
        viewport = ScheduleViewport.defaultScheduleViewport(
          sessionsByLocation.minDateTime.toEpochMinute(),
          sessionsByLocation.maxDateTime.toEpochMinute()
        )
      ) {
        for ((location, sessions) in sessionsByLocation) {
          // Create a track for the current location.
          StaggeredTrackLayout(
            modifier = Modifier
              .fillMaxWidth()
              .scheduleTrackStyle(),
            itemPadding = 8.dp,
          ) {
            sessions(
              location = location,
              sessions = sessions,
            ) { session ->
              key(session.hashCode()) {
                Session(
                  session = session,
                  modifier = Modifier.timeRange(
                    session.startTime.toEpochMinute(),
                    session.endTime.toEpochMinute()
                  ),
                  onClick = { shownSession = session }
                )
              }
            }
          }
        }
      }
    }

    // If user has tapped on a session, show popup with
    // session details. Exiting the popup clears the selection
    // and goes back to the full schedule.
    shownSession?.let { session ->
      SessionDetailsPopup(
        session = session,
        modifier = Modifier,
        onDismissRequest = { shownSession = null }
      )
    }
  }
}
