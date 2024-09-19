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

package rt.cccl.schedule.ui.track

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Constraints.Companion.Infinity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import rt.cccl.model.Session
import rt.cccl.model.ext.toEpochMinute
import rt.cccl.schedule.ext.overlaps
import rt.cccl.schedule.ui.schedule.ScheduleScope
import rt.cccl.schedule.ui.schedule.timeRange
import kotlin.math.roundToInt


fun TrackScope.sessions(
  location: String,
  sessions: ImmutableList<Session>,
  itemContent: @Composable (Session) -> Unit,
) = items(
  groupId = location,
  size = sessions.size,
  itemId = { index -> sessions[index].hashCode().toString() },
  timeRange = { index ->
    val session = sessions[index]
    session.startTime.toEpochMinute()..session.endTime.toEpochMinute()
  },
  itemContent = { index -> itemContent.invoke(sessions[index]) },
)

@Composable
internal fun ScheduleScope.StaggeredTrackLayout(
  modifier: Modifier = Modifier,
  itemPadding: Dp = 2.dp,
  content: TrackScope.() -> Unit,
) {
  // Generate a track scope used in emission of content.
  val trackScope = TrackScopeImpl(viewport)
    .apply { content() }

  // This layout allows us to subcompose a subset of the
  // track items based on the current viewport.
  SubcomposeLayout(modifier = modifier) { constraints ->
    val pxPerMinute: Float =
      if (constraints.maxWidth == Infinity) {
        1f
      } else {
        constraints.maxWidth.toFloat() / viewport.duration
      }

    val measurables = trackScope.itemGroups
      .flatMap { group ->
        val measurables = subcompose(slotId = group.groupId) {
          for (i in 0 until group.size) {
            group.itemContent.invoke(i)
          }
        }
        return@flatMap measurables
      }

    // Determine groups of overlapping items.
    var currentGroup = mutableListOf<Measurable>()
    val overlapGroups = mutableListOf<List<Measurable>>(currentGroup)
    measurables
      .forEach { measurable ->
        if (currentGroup.isEmpty() || currentGroup.last().timeRange.overlaps(measurable.timeRange)) {
          currentGroup.add(measurable)
        } else {
          currentGroup = mutableListOf(measurable)
          overlapGroups.add(currentGroup)
        }
      }

    val placeableGroups = overlapGroups.map { measurablesGroup ->
      measurablesGroup.map { measurable ->
        val startTime = measurable.timeRange.first
        val endTime = measurable.timeRange.last

        val minutes = endTime - startTime

        val width = (minutes * pxPerMinute).roundToInt()
        val height = measurable.minIntrinsicHeight(width)
        val sessionConstraints = Constraints.fixed(width, height)

        measurable.measure(sessionConstraints) to startTime..endTime
      }
    }

    val itemPaddingPx = itemPadding.roundToPx()
    val contentHeight = placeableGroups.maxOf { placeableGroup ->
      placeableGroup.sumOf { (placeable, _) ->
        placeable.height
      } + (placeableGroup.size - 1) * itemPaddingPx
    }

    // Do final placement.
    layout(constraints.maxWidth, contentHeight) {

      for (placeableGroup in placeableGroups) {
        var y = 0
        for ((placeable, timeRange) in placeableGroup) {
          if (timeRange.overlaps(viewport.timeRange)) {
            val x = ((timeRange.first - viewport.startTimeMinutes) * pxPerMinute).roundToInt()
            placeable.placeRelative(x = x, y = y)
          }
          y += placeable.height + itemPaddingPx
        }
      }
    }
  }
}
