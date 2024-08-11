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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import rt.yotei.model.Session
import rt.yotei.model.ext.toEpochMinute
import rt.yotei.schedule.ext.maxDateTime
import rt.yotei.schedule.ext.minDateTime
import rt.yotei.schedule.ui.closed.Schedule
import rt.yotei.schedule.ui.ScheduleViewport
import rt.yotei.schedule.ui.session.SessionDetailsPopup
import java.util.Locale

@Composable
fun CustomLayoutScheduleScreen(
  sessionsByLocation: ImmutableMap<String, ImmutableList<Session>>,
  onBack: (() -> Unit),
  modifier: Modifier = Modifier,
) {

  // Handle back gesture/button press.
  BackHandler { onBack() }

  if (sessionsByLocation.isNotEmpty()) {

    // The session selected to display details in a pop-up.
    var shownSession by remember { mutableStateOf<Session?>(null) }
    var viewport by remember {
      mutableStateOf(
        ScheduleViewport.defaultScheduleViewport(
          sessionsByLocation.minDateTime.toEpochMinute(),
          sessionsByLocation.maxDateTime.toEpochMinute()
        )
      )
    }
    Column(modifier = modifier) {
      // region // ==== Schedule Controls ====

      ScheduleControls(
        modifier = Modifier
          .border(
            width = 1.dp,
            color = Color.DarkGray
          )
          .windowInsetsPadding(WindowInsets.systemBars)
          .padding(bottom = 16.dp),
        onMaxDurationPercentChange = { maxDurationPercent ->
          viewport *= (maxDurationPercent * viewport.maxDuration) / viewport.duration
        },
      )

      // endregion

      Schedule(
        modifier = Modifier.fillMaxWidth(),
        sessionsByLocation = sessionsByLocation,
        viewport = viewport,
        onViewportChange = { viewport = it },
        onSessionClick = { session -> shownSession = session },
      )
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


@Composable
private fun ScheduleControls(
  onMaxDurationPercentChange: (Float) -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .padding(horizontal = 12.dp)
      .padding(bottom = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Text(
      text = "Scale",
      style = MaterialTheme.typography.labelMedium,
    )
    var sliderValue by remember { mutableFloatStateOf(.5f) }
    Slider(
      modifier = Modifier
        .weight(1f)
        .padding(start = 4.dp),
      onValueChange = { value ->
        sliderValue = value
        onMaxDurationPercentChange(value)
      },
      value = sliderValue,
      valueRange = 0.05f..1f,
    )
    Text(
      text = String.format(Locale.getDefault(), "%.3f", sliderValue),
      style = MaterialTheme.typography.labelMedium,
    )
  }
}