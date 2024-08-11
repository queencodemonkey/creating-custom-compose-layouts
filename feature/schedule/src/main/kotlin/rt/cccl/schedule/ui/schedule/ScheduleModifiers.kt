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

package rt.cccl.schedule.ui.schedule

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Density

// region // ==== Time Range Modifier ====

internal val Measurable.timeRange: LongRange?
  get() = (parentData as? ScheduleTimeRangeNode)?.startEndTimeRange


internal class ScheduleTimeRangeNode(
  var startEndTimeRange: LongRange,
) : ParentDataModifierNode, Modifier.Node() {
  override fun Density.modifyParentData(parentData: Any?) = this@ScheduleTimeRangeNode
}

internal data class ScheduleTimeRangeElement(
  val startEndTimeRange: LongRange,
  val inspectorInfo: InspectorInfo.() -> Unit,
) : ModifierNodeElement<ScheduleTimeRangeNode>() {

  override fun create(): ScheduleTimeRangeNode =
    ScheduleTimeRangeNode(startEndTimeRange)

  override fun update(node: ScheduleTimeRangeNode) {
    node.startEndTimeRange = startEndTimeRange
  }

  override fun InspectorInfo.inspectableProperties() {
    inspectorInfo()
  }
}

// endregion