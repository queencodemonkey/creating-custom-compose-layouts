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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import rt.cccl.model.ScheduleItem
import rt.cccl.model.Session
import rt.cccl.schedule.ui.schedule.ScheduleViewport

interface TrackScope {
  val viewport: ScheduleViewport

  fun items(
    groupId: String,
    size: Int,
    itemId: (Int) -> String,
    timeRange: (Int) -> LongRange,
    itemContent: @Composable (Int) -> Unit,
  )
}

@Composable
internal fun rememberTrackScope(
  viewport: ScheduleViewport,
): TrackScope = rememberSaveable(
  saver = TrackScopeImpl.Saver
) {
  TrackScopeImpl(viewport = viewport)
}

internal class TrackScopeImpl(
  viewport: ScheduleViewport,
) : TrackScope {

  private var _viewport by mutableStateOf(viewport)
  override var viewport: ScheduleViewport
    get() = _viewport
    set(value) {
      _viewport = value
    }

  private val _itemGroups: MutableList<TrackItemGroup> = mutableListOf()
  internal val itemGroups: ImmutableList<TrackItemGroup>
    get() = _itemGroups.toImmutableList()

  override fun items(
    groupId: String,
    size: Int,
    itemId: (Int) -> String,
    timeRange: (Int) -> LongRange,
    itemContent: @Composable (Int) -> Unit,
  ) {
    if (size < 1) return

    _itemGroups.add(
      TrackItemGroup(
        groupId = groupId,
        size = size,
        itemId = itemId,
        timeRange = timeRange,
        itemContent = { index -> itemContent(index) },
      )
    )
  }

  companion object {
    val Saver = Saver<TrackScopeImpl, ScheduleViewport>(
      save = { it.viewport },
      restore = { TrackScopeImpl(it) }
    )
  }
}

internal data class TrackItemGroup(
  val groupId: String,
  val size: Int,
  val itemId: (Int) -> String,
  val timeRange: (Int) -> LongRange,
  val itemContent: @Composable (Int) -> Unit,
)
