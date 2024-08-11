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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import rt.yotei.model.Session
import rt.yotei.model.ext.toEpochMinute
import rt.yotei.schedule.ext.maxDateTime
import rt.yotei.schedule.ext.minDateTime
import rt.yotei.schedule.ui.ScheduleViewport
import rt.yotei.schedule.ui.common.TrackLabel
import rt.yotei.schedule.ui.open.LayoutMode
import rt.yotei.schedule.ui.open.ScheduleLayout
import rt.yotei.schedule.ui.open.Track
import rt.yotei.schedule.ui.open.TrackScopeInstance.animateItemPlacement
import rt.yotei.schedule.ui.session.Session
import rt.yotei.schedule.ui.session.SessionDetailsPopup
import java.util.Locale

@Composable
fun OpenCustomLayoutScreen(
  sessionsByLocation: ImmutableMap<String, ImmutableList<Session>>,
  onBack: (() -> Unit),
  modifier: Modifier = Modifier,
) {
  // Handle back gesture/button press.
  BackHandler { onBack() }

  if (sessionsByLocation.isNotEmpty()) {
    LookaheadScope {
      // The session selected to display details in a pop-up.
      var shownSession by remember { mutableStateOf<Session?>(null) }

      Column(
        modifier = modifier,
      ) {
        var layoutMode by remember { mutableStateOf(LayoutMode.Overlap) }
        var viewport by remember {
          mutableStateOf(
            ScheduleViewport.defaultScheduleViewport(
              sessionsByLocation.minDateTime.toEpochMinute(),
              sessionsByLocation.maxDateTime.toEpochMinute()
            )
          )
        }
        // region // ==== Schedule Controls ====

        ScheduleControls(
          modifier = Modifier
            .border(
              width = 1.dp,
              color = Color.DarkGray
            ),
          onStaggeredChange = { staggered ->
            layoutMode = when {
              staggered -> LayoutMode.Stagger
              else -> LayoutMode.Overlap
            }
          },
          onMaxDurationPercentChange = { maxDurationPercent ->
            viewport *= (maxDurationPercent * viewport.maxDuration) / viewport.duration
          },
        )

        // endregion

        ScheduleLayout(
          modifier = Modifier.fillMaxWidth(),
          viewport = viewport,
          onViewportChange = { viewport = it },
        ) {
          var visible by remember { mutableStateOf(false) }
          LaunchedEffect(Unit) {
            visible = true
          }
          for ((location, sessions) in sessionsByLocation) {
            TrackLabel(
              modifier = Modifier.animateItemPlacement(this@LookaheadScope),
              text = location,
            )
            // Create a track for the current location.
            Track(
              modifier = Modifier.fillMaxWidth(),
              mode = layoutMode,
            ) {
              for (session in sessions) {
                key(session.hashCode()) {
                  Session(
                    session = session,
                    modifier = Modifier
                      .timeRange(
                        startTime = session.start,
                        endTime = session.end,
                      )
                      .animateItemPlacement(this@LookaheadScope),
                    onClick = { shownSession = session }
                  )
                }
              }
            }
          }
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
      }
    }
  }
}

@Composable
private fun ScheduleControls(
  onStaggeredChange: (Boolean) -> Unit,
  onMaxDurationPercentChange: (Float) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

    Row(
      modifier = Modifier.padding(start = 12.dp, bottom = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        text = "Overlapped",
        style = MaterialTheme.typography.labelMedium,
      )

      var switchValue by remember { mutableStateOf(false) }
      Switch(
        checked = switchValue,
        onCheckedChange = {
          switchValue = it
          onStaggeredChange(it)
        }
      )
      Text(
        text = "Staggered",
        style = MaterialTheme.typography.labelMedium,
      )
    }
    Row(
      modifier = Modifier
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
}
