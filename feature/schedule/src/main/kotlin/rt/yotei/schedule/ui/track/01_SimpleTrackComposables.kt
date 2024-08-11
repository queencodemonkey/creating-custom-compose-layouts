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

package rt.yotei.schedule.ui.track

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import kotlinx.collections.immutable.ImmutableList
import rt.yotei.model.Session
import rt.yotei.model.ext.minutesBetween
import rt.yotei.model.ext.overallDuration
import rt.yotei.schedule.ext.times
import rt.yotei.schedule.ui.session.Session
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

// region // ==== Row Implementation ====

/**
 * Single schedule track. Implemented via [Row].
 *
 * [Spacer] instances are used for gaps between sessions.
 * Note: this is an example for presentation purpose and not a rigorous implementation
 * as it does not account for cases such as session overlap etc.
 *
 * @param sessions List of sessions; the data.
 * @param modifier The modifier to be applied to the layout.
 * @param dpsPerMinute Essentially the zoom/scale at which we are displaying time on-screen.
 * @param onSessionClick Called when a session is clicked.
 */
@Composable
fun ScheduleRowTrack(
  sessions: ImmutableList<Session>,
  dpsPerMinute: Dp,
  modifier: Modifier = Modifier,
  onSessionClick: (Session) -> Unit = {},
) {
  Row(modifier = modifier) {
    // Min start time of any session (sessions is assumed to be sorted).
    val startTime = sessions.first().startTime

    // Create spacer between the start of the schedule
    // and the start of the first session.
    val minutesTillFirstSession = minutesBetween(startTime, sessions.first())
    if (minutesTillFirstSession > 0) {
      val spacerWidth: Dp = dpsPerMinute * minutesTillFirstSession
      Spacer(modifier = Modifier.width(spacerWidth))
    }

    // Tracker for the end time of the last session processed.
    var lastTime: LocalDateTime? = null

    // Compose each session.
    for (session in sessions) {
      if (lastTime != null) {
        // Calculate spacer width for free time between sessions.
        val spacerMinutes: Int = minutesBetween(lastTime, session)
        if (spacerMinutes > 0) {
          val spacerWidth: Dp = dpsPerMinute * spacerMinutes
          Spacer(modifier = Modifier.width(spacerWidth))
        }
      }

      // Calculate the width of the session based on the zoom level and
      // the duration of the session in minutes.
      val width = dpsPerMinute * session.duration.toInt()
      key(session.hashCode()) {
        Session(
          modifier = Modifier.width(width),
          session = session,
          onClick = { onSessionClick(session) }
        )
      }

      // Update our tracker. This will be the start of the next spacer.
      lastTime = session.endTime
    }
  }
}

// endregion

// region // ==== LazyRow Implementation ====

/**
 * Single schedule track. Implemented via [Row].
 *
 * [Spacer] instances are used for gaps between sessions.
 * Note: this is an example for presentation purpose and not a rigorous implementation
 * as it does not account for cases such as session overlap etc.
 *
 * @param sessions List of sessions; the data.
 * @param modifier The modifier to be applied to the layout.
 * @param dpsPerMinute Essentially the zoom/scale at which we are displaying time on-screen.
 * @param onSessionClick Called when a session is clicked.
 */
@Composable
fun ScheduleLazyRowTrack(
  sessions: ImmutableList<Session>,
  dpsPerMinute: Dp,
  modifier: Modifier = Modifier,
  onSessionClick: (Session) -> Unit = {},
) {
  LazyRow(modifier = modifier) {
    // Min start time of any session (sessions is assumed to be sorted).
    val startTime = sessions.first().startTime

    // Create spacer between the start of the schedule
    // and the start of the first session.
    val minutesTillFirstSession = minutesBetween(startTime, sessions.first())
    if (minutesTillFirstSession > 0) {
      val spacerWidth: Dp = dpsPerMinute * minutesTillFirstSession
      item {
        key("first_spacer") {
          Spacer(modifier = Modifier.width(spacerWidth))
        }
      }
    }

    // Tracker for the end time of the last session processed.
    var lastTime: LocalDateTime? = null
    // Compose each session.
    for (session in sessions) {
      if (lastTime != null) {
        // Calculate spacer width for free time between sessions.
        val spacerMinutes: Int = minutesBetween(lastTime, session)
        if (spacerMinutes > 0) {
          val spacerWidth: Dp = dpsPerMinute * spacerMinutes
          item {
            key("${session.hashCode()}_spacer") {
              Spacer(modifier = Modifier.width(spacerWidth))
            }
          }
        }
      }
      // Calculate the width of the session based on the zoom level and
      // the duration of the session in minutes.
      val width = dpsPerMinute * session.duration
      item {
        key(session.hashCode()) {
          Session(
            modifier = Modifier.width(width),
            session = session,
            onClick = { onSessionClick(session) },
          )
        }
      }
      // Update our tracker. This will be the start of the next spacer.
      lastTime = session.endTime
    }
  }
}

// endregion

// region // ==== Box Implementation ====

/**
 * Single schedule track. Implemented via [Box].
 *
 * Position is specified via calculating an absolute offset.
 *
 * @param sessions List of sessions; the data.
 * @param modifier The modifier to be applied to the layout.
 * @param dpsPerMinute Essentially the zoom/scale at which we are displaying time on-screen.
 * @param onSessionClick Called when a session is clicked.
 */
@Composable
fun ScheduleBoxTrack(
  sessions: ImmutableList<Session>,
  dpsPerMinute: Dp,
  modifier: Modifier = Modifier,
  onSessionClick: (Session) -> Unit = {},
) {
  if (sessions.isNotEmpty()) {
    // Min start time of any session (sessions is assumed to be sorted).
    val startTime = sessions.first().startTime
    // Max end time of any session.
    val endTime = sessions.maxOf(Session::endTime)

    // Total minutes covered by all the sessions.
    val timeSpanMinutes = ChronoUnit.MINUTES.between(startTime, endTime).toInt()

    // Because we are using offsets here, we need to do more
    // manual implementation of the scrolling.
    var offset by remember { mutableFloatStateOf(0f) }
    BoxWithConstraints(
      modifier = modifier
        .scrollable(
          orientation = Orientation.Horizontal,
          state = rememberScrollableState { delta ->
            offset += delta
            offset = offset.coerceAtMost(0f)
            delta
          }
        )
    ) {
      // Convert session start times to X-values and use those
      // as absoluteOffset values.
      val maxOffset = -dpsPerMinute * timeSpanMinutes + maxWidth

      // Compose each session.
      for (session in sessions) {
        // Adjusts layout position based on how much has been scrolled.
        val offsetDp = with(LocalDensity.current) { offset.toDp() }.coerceIn(maxOffset, 0.dp)
        // Width of the Composable for this session based on the session's duration.
        val width: Dp = dpsPerMinute * session.duration
        key(session.hashCode()) {
          Session(
            modifier = Modifier
              .width(width)
              .offset {
                // Final x-coordinate of the Composable for this session.
                val x: Dp = offsetDp + minutesBetween(startTime, session) * dpsPerMinute
                return@offset IntOffset(x.roundToPx(), 0)
              }
              .wrapContentHeight(),
            session = session,
            onClick = { onSessionClick(session) },
          )
        }
      }
    }
  }
}

// endregion

// region // ==== Layout Implementation ====

/**
 * Single schedule track. Implemented via a custom [Layout].
 *
 * Note: here we can take advantage of phased state reads by
 * separating the state calculation for measure/placement out of
 * composition and into the layout phase thus allowing for
 * some optimization by Compose.
 *
 * @param sessions List of sessions; the data.
 * @param modifier The modifier to be applied to the layout.
 * @param dpsPerMinute Essentially the zoom/scale at which we are displaying time on-screen.
 * @param onSessionClick Called when a session is clicked.
 */
@Composable
fun ScheduleLayoutTrack(
  sessions: ImmutableList<Session>,
  dpsPerMinute: Dp,
  modifier: Modifier = Modifier,
  onSessionClick: (Session) -> Unit = {},
) {
  Layout(
    modifier = modifier,
    content = {
      // region // ==== Composition ====

      // Compose each session.
      for (session in sessions) {
        key(session.hashCode()) {
          Session(
            modifier = Modifier
              // Pass session data to the layout phase.
              // Yes, this is kinda gross. We'll talk about this.
              .layoutId(session),
            session = session,
            onClick = { onSessionClick(session) }
          )
        }
      }

      // endregion
    },
    measurePolicy = { measurables, constraints ->

      // region // === Measurement Phase ===

      // DPs/minute is in effect our zoom. Layout phase is pixel-relative
      // so convert to pixels.
      val pxPerMinute = dpsPerMinute.toPx()

      // 1. Measure each child/each emission from the composition phase: measurables.
      // 2. Receive a placeable corresponding to each measurable that now has width/height
      //    information.
      val placeables = measurables.map { measurable ->
        // Pull out session time information that we used to figure out
        //   how wide each session should be and where it should be placed.
        val session = measurable.layoutId as Session
        val minutes = session.duration
        val widthPx = (minutes * pxPerMinute).roundToInt()
        // Use the width information we calculated for the session and create constraints
        //   for the measuring of this measurable.

        // region // ==== About Constraints ====

        // Constraints define the bounds (or lack there of) for the
        // dimensions of a measurable.

        // These constraints define an unbounded width and unbounded height,
        //   meaning the child can be however big as they want.
        // Dimensions CANNOT be < 0, or you get a crash.
//        Constraints(
//          minWidth = 0,
//          maxWidth = Constraints.Infinity,
//          minHeight = 0,
//          maxHeight = Constraints.Infinity
//        )
//        // These constraints define a child where the width must be at least 400px,
//        //   and the height must be no more than 200px.
//        Constraints(
//          minWidth = 400,
//          maxWidth = Constraints.Infinity,
//          minHeight = 0,
//          maxHeight = 200
//        )

        // endregion

        // These constraints define our session children to be fixed width,
        // and to follow the constraints passed into the parent.
        val sessionConstraints = constraints.copy(
          minWidth = widthPx,
          maxWidth = widthPx
        )

        // Measure the child with the constraints (fixed width, unbounded height).
        measurable.measure(sessionConstraints)
      }

      // Pull out session data for each measurable into a list
      //   so we can match placeable to session by index.
      val measuredSessions = measurables.map { it.layoutId as Session }

      // region // ==== Nothing to see here… yet. ====




      // What if we need to have all of the sessions be equal height?




       // region // ==== About Intrinsic measurements ====


      // In one frame, you can only measure children (call `measurable.measure()`) ONCE.

      //   Calling `measure()` more than once on the same child results in a crash.

      //   In Compose, measurement is not just an observational process like in Views:
      //   it DEFINES the size of that child.




      // But what if you need information about what sizes the children could be
      //   to make a final decision on measurement?




      // INTRINSICS.




      // Given this (min or max) width, how high would this child be?
      // Given this (min or max) height, how wide would this child be?
      // Requesting intrinsics does NOT count as a measure.




      // endregion

//      // Create a corresponding array of the session data for each measurable.
//      // Still gross, I know.
//      val measuredSessions = measurables.map { it.layoutId as Session }
//
//      // Pull out session time information that we used to figure out
//      //   how wide each session should be.
//      val widths = sessions.map { session ->
//        (session.duration * pxPerMinute).roundToInt()
//      }
//
//      // Use intrinsics to pre-calculate the height given the width.
//      val intrinsicHeights = measurables.mapIndexed { index, measurable ->
//        val width = widths[index]
//        measurable.minIntrinsicHeight(width)
//      }
//
//      // Take the max of the intrinsic heights and make it THE height
//      //   of all the children.
//      val height = intrinsicHeights.max()
//
//      val placeables = measurables.mapIndexed { index, measurable ->
//        // Use fixed constraints to put together our pre-calculated
//        // widths and heights.
//        val sessionConstraints = Constraints.fixed(widths[index], height)
//        measurable.measure(sessionConstraints)
//      }

      // endregion

      // We need to specify how big this Composable (Schedule Track) should be.
      // So we calculate the total time covered by all sessions, multiply by zoom,
      // and round to the nearest pixel.
      val contentWidth = (measuredSessions.overallDuration * pxPerMinute).roundToInt()
      // Height is going to be the max height of any of the children.
      val contentHeight = placeables.maxOf(Placeable::height)

      // endregion

      // region // === Placement Phase ===
      layout(contentWidth, constraints.constrainHeight(contentHeight)) {
        // Min start time of any session (sessions is assumed to be sorted).
        val startTime = sessions.first().startTime

        // Referencing the original session data object,
        // calculate the x-position of each child in the schedule track.
        val xCoordinates: List<Int> =
          if (measuredSessions.isEmpty()) {
            emptyList()
          } else {
            measuredSessions.map { session ->
              (minutesBetween(startTime, session) * pxPerMinute).roundToInt()
            }
          }
        // Place each child in the schedule track at a specific x and y.
        // Here, we're just laying out sessions in a row, so they're all at y = 0.
        placeables.forEachIndexed { index, placeable ->
          placeable.placeRelative(x = xCoordinates[index], y = 0)
        }
      }
      // endregion
    }
  )
}

// endregion
