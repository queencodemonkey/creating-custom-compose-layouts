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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import rt.yotei.model.Session
import rt.yotei.schedule.ui.common.TrackLabel
import rt.yotei.schedule.ui.common.scheduleTrackStyle
import rt.yotei.schedule.ui.session.SessionDetailsPopup
import rt.yotei.schedule.ui.track.ScheduleLazyRowTrack
import rt.yotei.schedule.ui.track.ScheduleRowTrack
import java.time.LocalDateTime
import java.util.Locale

@Preview(showSystemUi = true)
@Composable
private fun SimpleSchedulePreview() {
  MaterialTheme {
    Surface {
      val sessions = listOf(
        Session(
          name = "Daily Dynamic Ladder Flow",
          instructor = "Briohny Smyth",
          startTime = LocalDateTime.parse("2024-09-08T07:00:00"),
          endTime = LocalDateTime.parse("2024-09-08T08:00:00"),
          location = "Yoga Studio",
          category = Session.Category.MindBody
        ),
        Session(
          name = "Shreds - Upper Body",
          instructor = "Sam Miller",
          startTime = LocalDateTime.parse("2024-09-08T08:30:00"),
          endTime = LocalDateTime.parse("2024-09-08T10:00:00"),
          location = "Main Gym",
          category = Session.Category.Strength
        ),
        Session(
          name = "Doc's Fitness",
          instructor = "AJ Holland",
          startTime = LocalDateTime.parse("2024-09-08T11:00:00"),
          endTime = LocalDateTime.parse("2024-09-08T12:00:00"),
          location = "Boathouse",
          category = Session.Category.Strength,

          ),
        Session(
          name = "Lower Body Barre",
          instructor = "Corina Lindley",
          startTime = LocalDateTime.parse("2024-09-08T12:30:00"),
          endTime = LocalDateTime.parse("2024-09-08T13:15:00"),
          location = "Yoga Studio",
          category = Session.Category.Dance
        ),
        Session(
          name = "Beginning Hip Hop",
          instructor = "Marguerite Hensley",
          startTime = LocalDateTime.parse("2024-09-08T14:00:00"),
          endTime = LocalDateTime.parse("2024-09-08T15:15:00"),
          location = "Dance Studio",
          category = Session.Category.Dance
        ),
      ).toImmutableList()
      SimpleScheduleScreen(
        sessions = sessions,
        onBack = {},
        modifier = Modifier.fillMaxSize()
      )
    }
  }
}

/**
 * Schedule displaying all of the [sessions] in a single track.
 *
 * Shows multiple versions of the same track with different
 * underlying implementation.
 *
 * @param onBack Called on system back.
 * @param modifier The modifier to be applied to the screen.
 */
@Composable
fun SimpleScheduleScreen(
  sessions: ImmutableList<Session>,
  onBack: (() -> Unit),
  modifier: Modifier = Modifier,
) {
  // Handle back gesture/button press.
  BackHandler { onBack() }

  if (sessions.isNotEmpty()) {
    Box(modifier = Modifier.fillMaxSize()) {
      // The session selected to display details in a pop-up.
      var shownSession by remember { mutableStateOf<Session?>(null) }

      Column(modifier = modifier.verticalScroll(rememberScrollState())) {

        // region // ==== DPs/Minutes slider ====

        // To control the "zoom" of the schedule (how much time we are showing
        // on the screen, we have a slider that shows "DPs/minute".
        var dpsPerMinuteValue by remember { mutableFloatStateOf(3f) }
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        TrackLabel(
          text = "DPs/minute:" + String.format(
            locale = Locale.getDefault(),
            format = "%2.1f",
            dpsPerMinuteValue
          ),
        )
        // DPs/minute slider to zoom in and out.
        // More DPs/minute = wider rendering of sessions.
        Slider(
          modifier = Modifier.padding(horizontal = 16.dp),
          valueRange = 1f..5f,
          value = dpsPerMinuteValue,
          onValueChange = { dpsPerMinuteValue = it },
        )

        // endregion

        // region // === Row Implementation ===

        TrackLabel(
          text = "Row Implementation",
        )
        // Schedule track implemented with a Row.
        ScheduleRowTrack(
          modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .scheduleTrackStyle(),
          sessions = sessions,
          dpsPerMinute = dpsPerMinuteValue.dp,
          onSessionClick = { session -> shownSession = session }
        )

        // endregion

        // region // === LazyRow Implementation ===

        TrackLabel(
          text = "LazyRow Implementation",
        )
        // Schedule track implemented with a LazyRow.
        ScheduleLazyRowTrack(
          modifier = Modifier.scheduleTrackStyle(),
          sessions = sessions,
          dpsPerMinute = dpsPerMinuteValue.dp,
          onSessionClick = { session -> shownSession = session }
        )

        // endregion

        // region // === BoxWithConstraints Implementation ===

//        TrackLabel(
//          text = "BoxWithConstraints Implementation",
//        )
//        // Schedule track implemented with a Box.
//        ScheduleBoxTrack(
//          modifier = Modifier
//            .fillMaxWidth()
//            .scheduleTrackStyle(),
//          sessions = sessions,
//          dpsPerMinute = dpsPerMinuteValue.dp,
//          onSessionClick = { session -> shownSession = session }
//        )

        // endregion

        // region // === Layout Implementation ===

//        TrackLabel(
//          text = "Layout Implementation",
//        )
//        // Schedule track implemented with a custom Layout.
//        ScheduleLayoutTrack(
//          modifier = Modifier
//            .horizontalScroll(rememberScrollState())
//            .scheduleTrackStyle(),
//          sessions = sessions,
//          dpsPerMinute = dpsPerMinuteValue.dp,
//          onSessionClick = { session -> shownSession = session }
//        )

        // endregion

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
  }
}
