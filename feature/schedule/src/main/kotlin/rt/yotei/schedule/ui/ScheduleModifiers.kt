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

package rt.yotei.schedule.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Density

// region // ==== Time Range Modifier ====

/**
 * Convenience function for accessing a [TimeRangeNode]
 * from a [Measurable].
 */
internal val Measurable.timeRange: LongRange
  get() = (parentData as TimeRangeNode).timeRange

/**
 * Convenience function for accessing a [TimeRangeNode]
 * from a [Placeable].
 */
internal val Placeable.timeRange: LongRange
  get() = (parentData as TimeRangeNode).timeRange

/**
 * The longer-lived, state-holding, logic executing
 * part of a custom modifier.
 *
 * Mutable and responds to changes in the value passed
 * to the modifier function.
 */
internal class TimeRangeNode(
  var timeRange: LongRange,
) : ParentDataModifierNode, Modifier.Node() {
  override fun Density.modifyParentData(parentData: Any?) = this@TimeRangeNode
}

/**
 * Created everytime a modifier function is called.
 *
 * Short-lived, lightweight part of a custom modifier that:
 * - Creates a new element
 * - Updates the existing element
 */
internal data class TimeRangeElement(
  val startEndTimeRange: LongRange,
  val inspectorInfo: InspectorInfo.() -> Unit,
) : ModifierNodeElement<TimeRangeNode>() {

  override fun create(): TimeRangeNode =
    // Create a new instance of our time range node.
    TimeRangeNode(startEndTimeRange)

  override fun update(node: TimeRangeNode) {
    // Update the existing node with the new time range.
    //
    // When we move our viewport around our schedule,
    //   our Modifier.timeRange() function will be
    //   called on recomposition, and this function
    //   will be called to update the existing node.
    node.timeRange = startEndTimeRange
  }

  override fun InspectorInfo.inspectableProperties() {
    // Provides tool-inspectable values.
    inspectorInfo()
  }
}

// endregion