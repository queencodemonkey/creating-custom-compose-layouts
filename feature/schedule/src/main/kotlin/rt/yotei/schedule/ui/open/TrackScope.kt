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

package rt.yotei.schedule.ui.open

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Density

/**
 * Receiver scope for children of schedule track
 * composables. Provides viewport information to
 * children whose measure/layout depends on the
 * viewport.
 */
interface TrackScope {
  /**
   * Modifier that allows a child to specify its
   * start/end time on the schedule.
   *
   * @param startTime a child's start time (minutes since the epoch).
   * @param endTime a child's end time (minutes since the epoch).
   */
  fun Modifier.timeRange(
    startTime: Long,
    endTime: Long,
  ): Modifier

  /**
   * Modifier that animates the placement of items in this track.
   */
  fun Modifier.animateItemPlacement(lookaheadScope: LookaheadScope): Modifier
}

/**
 * Singleton implementation of [TrackScope].
 */
internal object TrackScopeInstance : TrackScope {
  override fun Modifier.timeRange(startTime: Long, endTime: Long) =
    then(TrackTimeRangeElement(
      startEndTimeRange = startTime..endTime,
      inspectorInfo = {
        name = "timeRange"
        properties["startTimeMinutes"] = startTime
        properties["endTimeMinutes"] = endTime
      }
    ))

  override fun Modifier.animateItemPlacement(lookaheadScope: LookaheadScope) =
    then(AnimatePlacementNodeElement(lookaheadScope))
}

// region // ==== Time Range Modifier ====

/**
 * Convenience function for accessing a [ScheduleTimeRangeNode]
 * from a [Measurable].
 */
internal val Measurable.timeRange: LongRange?
  get() = (parentData as? ScheduleTimeRangeNode)?.timeRange

/**
 * Convenience function for accessing a [ScheduleTimeRangeNode]
 * from a [Placeable].
 */
internal val Placeable.timeRange: LongRange?
  get() = (parentData as? ScheduleTimeRangeNode)?.timeRange

/**
 * The longer-lived, state-holding portion of the
 * [TrackScope.timeRange] modifier.
 *
 * Mutable and responds to changes in the time range
 * passed to the modifier function and provides that
 * time range as parent data.
 */
private class ScheduleTimeRangeNode(
  var timeRange: LongRange,
) : ParentDataModifierNode, Modifier.Node() {
  override fun Density.modifyParentData(parentData: Any?) =
    this@ScheduleTimeRangeNode
}

/**
 * Created everytime the [TrackScope.timeRange]
 * modifier is called.
 *
 * Short-lived, lightweight part of the modifier that:
 * - Creates a new element
 * - Updates the existing element with changes to time range.
 */
private class TrackTimeRangeElement(
  val startEndTimeRange: LongRange,
  val inspectorInfo: InspectorInfo.() -> Unit,
) : ModifierNodeElement<ScheduleTimeRangeNode>() {

  override fun create(): ScheduleTimeRangeNode =
    ScheduleTimeRangeNode(startEndTimeRange)

  override fun update(node: ScheduleTimeRangeNode) {
    // When we move our viewport around our schedule,
    //   our Modifier.timeRange() function will be
    //   called on recomposition, and this function
    //   will be called to update the existing node.
    node.timeRange = startEndTimeRange
  }

  override fun equals(other: Any?): Boolean =
    this === other ||
        (other is TrackTimeRangeElement && other.startEndTimeRange == startEndTimeRange)

  override fun hashCode() = startEndTimeRange.hashCode()

  override fun InspectorInfo.inspectableProperties() {
    // Provides tool-inspectable values.
    inspectorInfo()
  }
}

// endregion