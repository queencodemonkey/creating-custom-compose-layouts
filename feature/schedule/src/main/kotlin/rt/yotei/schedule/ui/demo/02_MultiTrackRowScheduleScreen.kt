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

package rt.yotei.schedule.ui.demo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import rt.yotei.model.Session
import rt.yotei.schedule.ui.common.TrackLabel
import rt.yotei.schedule.ui.common.scheduleTrackStyle
import rt.yotei.schedule.ui.session.SessionDetailsPopup
import rt.yotei.schedule.ui.track.ScheduleLayoutTrack
import java.util.Locale

/**
 * Schedule that groups sessions by locations into
 * separate horizontal tracks.
 */
@Composable
fun MultiTrackRowScheduleScreen(
  sessions: ImmutableList<Session>,
  onBack: (() -> Unit),
  modifier: Modifier = Modifier,
) {
  // Handle back gesture/button press.
  BackHandler { onBack() }

  // The session selected to display details in a pop-up.
  var shownSession by remember { mutableStateOf<Session?>(null) }

  // All schedule tracks laid out vertically.
  Column(
    modifier = modifier.verticalScroll(rememberScrollState())
  ) {
    Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

    // region // ==== DPs/Minutes slider ====

    var dpsPerMinuteValue by remember { mutableFloatStateOf(3f) }
    TrackLabel(
      text = "DPs/minute:" + String.format(
        locale = Locale.getDefault(),
        format = "%2.1f",
        dpsPerMinuteValue
      ),
    )

    // DPs/minute slider to zoom in and out.
    Slider(
      modifier = Modifier.padding(horizontal = 16.dp),
      valueRange = 1f..5f,
      value = dpsPerMinuteValue,
      onValueChange = { dpsPerMinuteValue = it },
    )

    // endregion

    // Schedule Tracks
    // Group sessions by location.
    val locationSessionsMap = sessions
      .groupBy(Session::location)
      .mapValues { (_, sessions) -> sessions.toImmutableList() }

    // Shared scroll state…?
    val trackScrollState = rememberScrollState()

    for (location in locationSessionsMap.keys) {
      // Retrieve all the sessions for a given location.
      val locationSessions = locationSessionsMap[location] ?: persistentListOf()
      TrackLabel(text = location)
      ScheduleLayoutTrack(
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
          // Using the same scroll state for all tracks.
          .horizontalScroll(trackScrollState)
          .scheduleTrackStyle(),
        sessions = locationSessions,
        dpsPerMinute = dpsPerMinuteValue.dp,
        onSessionClick = { session -> shownSession = session }
      )
    }

    // Because edge-to-edge and stuff now.
    Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
  }

  // region // ==== Session Popup ====

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



  // endregion
}