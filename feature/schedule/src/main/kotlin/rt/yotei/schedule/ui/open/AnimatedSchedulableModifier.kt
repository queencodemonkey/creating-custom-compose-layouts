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

@file:OptIn(ExperimentalAnimatableApi::class)

package rt.yotei.schedule.ui.open

import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.DeferredTargetAnimation
import androidx.compose.animation.core.ExperimentalAnimatableApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ApproachLayoutModifierNode
import androidx.compose.ui.layout.ApproachMeasureScope
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round

/**
 * This code was taken directly from the example in the developer docs for
 * [LookaheadScope]:
 * https://developer.android.com/reference/kotlin/androidx/compose/ui/layout/LookaheadScope
 *
 * Creates a custom implementation of ApproachLayoutModifierNode to approach the placement of
 * the layout using an animation.
 */
class AnimatedPlacementModifierNode(var lookaheadScope: LookaheadScope) :
  ApproachLayoutModifierNode, Modifier.Node() {
  // Creates an offset animation, the target of which will be known during placement.
  private val offsetAnimation: DeferredTargetAnimation<IntOffset, AnimationVector2D> =
    DeferredTargetAnimation(IntOffset.VectorConverter)

  override fun isMeasurementApproachInProgress(lookaheadSize: IntSize): Boolean {
    // Since we only animate the placement here, we can consider measurement approach
    // complete.
    return false
  }

  // Returns true when the offset animation is in progress, false otherwise.
  override fun Placeable.PlacementScope.isPlacementApproachInProgress(
    lookaheadCoordinates: LayoutCoordinates
  ): Boolean {
    val target =
      with(lookaheadScope) {
        lookaheadScopeCoordinates.localLookaheadPositionOf(lookaheadCoordinates).round()
      }
    offsetAnimation.updateTarget(
      target,
      coroutineScope,
      tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )
    return !offsetAnimation.isIdle
  }

  override fun ApproachMeasureScope.approachMeasure(
    measurable: Measurable,
    constraints: Constraints
  ): MeasureResult {
    val placeable = measurable.measure(constraints)
    return layout(placeable.width, placeable.height) {
      val coordinates = coordinates
      if (coordinates != null) {
        // Calculates the target offset within the lookaheadScope
        val target =
          with(lookaheadScope) {
            lookaheadScopeCoordinates.localLookaheadPositionOf(coordinates).round()
          }

        // Uses the target offset to start an offset animation
        val animatedOffset = offsetAnimation.updateTarget(target, coroutineScope)
        // Calculates the *current* offset within the given LookaheadScope
        val placementOffset =
          with(lookaheadScope) {
            lookaheadScopeCoordinates
              .localPositionOf(coordinates, Offset.Zero)
              .round()
          }
        // Calculates the delta between animated position in scope and current
        // position in scope, and places the child at the delta offset. This puts
        // the child layout at the animated position.
        val (x, y) = animatedOffset - placementOffset
        placeable.place(x, y)
      } else {
        placeable.place(0, 0)
      }
    }
  }
}

// Creates a custom node element for the AnimatedPlacementModifierNode above.
data class AnimatePlacementNodeElement(val lookaheadScope: LookaheadScope) :
  ModifierNodeElement<AnimatedPlacementModifierNode>() {

  override fun update(node: AnimatedPlacementModifierNode) {
    node.lookaheadScope = lookaheadScope
  }

  override fun create(): AnimatedPlacementModifierNode {
    return AnimatedPlacementModifierNode(lookaheadScope)
  }

  override fun InspectorInfo.inspectableProperties() {
    name = "animateSchedulable"
    properties["lookaheadScope"] = lookaheadScope
  }
}
