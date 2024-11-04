# Introduction
This is code from my Droidcon presentation, "Creating Custom Compose Layouts".

## Demo Basics
### Data
To swap data displayed in the schedule:
1. Open `rt.yotei.network.demo.DemoGymDataSource`
2. Go to L51 where `inputStream` is declared.
3. Change resource to:
    - `R.raw.fitness_classes_01_nol`: smaller data set with no overlapping classes; used for initial demos
    - `R.raw.fitness_classes_02_ol`: larger data set with overlapping classes; used to show issues with overlapping classes and how those classes present in various implementations
### Composables
The common `@Composable` among all the demos is the `@Composable Session` which renders a singular class inside of the schedule. It is backed by a `Session` data object.

# 1. Basic Schedule Track Implementations

## App Examples

To view Schedule Track implementations using Jetpack Compose framework layouts:
1. Swap demo data to `R.raw.fitness_classes_01_nol`.
2. Go to `01_SimpleScheduleScreen.kt`.
3. View code regions that delineate various framework layout version of the track:
    -  `ScheduleRowTrack`
    -  `ScheduleLazyRowTrack`
    -  `ScheduleBoxWithConstraintsTrack`
4. Enable (uncomment) desired implementations.
> **Note**: `ScheduleRowTrack` and `ScheduleLazyRowTrack` were discussed during Droidcon London.
5. Run the application.
6. Select the "Blue" demo.

To view how many recompositions occur when changing the DPs/minute:
1. Enable the Layout Inspector.
2. Select the "Blue" demo.
3. Drill down to the `Schedule<X>Track` implementation of interest in the Layout Inspector and further to see the `Session` instances.
4. Move the DPs/minute slider.
5. Examine the recomposition counts in the Layout Inspector.
## Code

To view the individual implementations of the `Schedule<X>Track` composables.
1. Open the `01_SimpleTrackCompsables.kt` file.
2. Scroll down to the desired `@Composable Schedule<X>Track`.

## Things to Note
- The `ScheduleRowTrack`, `ScheduleLazyRowTrack`, and the `ScheduleBoxWithConstraintsTrack` all recompose frequently as DPs/minute changes.
- The `ScheduleLazyRowTrack` will change height depending on the currently visible `Session` instances.
# 2. Basic Layout Schedule Track Implementation

## App Example

To view the custom layout Schedule Track implementation in the sample application:
1. Go to `01_SimpleScheduleScreen.kt`.
2. Enable (uncomment) the `ScheduleLayoutTrack` code region.
3. Run the application.
4. Select the "Blue" demo.

To view how many recompositions are skipped when changing the DPs/minute:
1. Enable the Layout Inspector.
2. Select the "Blue" demo.
3. Drill down to the `ScheduleLayoutTrack` implementation of interest in the Layout Inspector and further to see the `Session` instances.
4. Move the DPs/minute slider.
5. Examine the recomposition counts in the Layout Inspector.
## Code

To view the custom layout Schedule Track code:
1. Go to `01_SimpleTrackCompsables`.
2. Scroll down to `@Composable ScheduleLayoutTrack`.

Continue to next section for walkthrough of the `ScheduleLayoutTrack` which is the first, simplest example of a `@Composable Layout`-based custom layout.

# 3. Custom Layout Composition, Measure, and Placement

> **Note** There are several nested code regions within the `@Composable ScheduleLayoutTrack`
> The committed code in the repo represents the "start state" for the presentation. The proceeding instructions assume this state.

## Composition
- If folded, unfold the `Composition` code region.
- Composition contains just the code to define what content will be displayed within the `ScheduleLayoutTrack`.

## Measurement
- Measurement lives inside of the `measurePolicy` parameter definition.
- If folded, unfold the `Measurement` code region.
- Measurement phase contains comments to explain step-by-step the measure process.

### Code Region: About Constraints
The `About Constraints` sub-code-region contains information about how `Constraints` work along with some examples that may be uncommented to see syntax highlight and for drilling into the code.

### Code Region: Nothing to see here… yet.
If following along, fold this region for now. It contains code and information related to [Intrinsics](#intrinsics).

## Placement
- Placement lives inside of the call to `layout` inside of the `measurePolicy` parameter definition.
- If folded, unfold the `Placement` code region.
- Placement phase contains comments to explain step-by-step the placement process.

## Intrinsics
- Intrinsics allow us to do a pre-measurement under certain circumstances if we need the dimensions of the custom layout or its children to depend on the intrinsic width/height of children.
- If folded, unfold the `Nothing to see here… yet` region inside of `Measurement`
### Code Region: About Intrinsics
Inside of `Nothing to see here… yet` region is `About Intrinsics` code region. This region contains information about Intrinsics.

### Code: Using Intrinsics to Determine Max Session Height
1. Inside of `Nothing to see here… yet` code region, uncomment the code there to use intrinsics to determine the height of the tallest `@Composable Session.`
2. Comment out the code between the two statements:
    - `val pxPerMinute = dpsPerMinute.toPx()`
    - The `// endregion` of the `About Intrinsics` code region.
3. Run the application.
4. Select the "Blue" demo.
5. Observe the height of the `ScheduleLayoutTrack` children.

# 4. Multi-Track Schedule with Shared Scroll State

## Overlapping Data with Simple Track Composables

1. Open `rt.yotei.network.demo.DemoGymDataSource`
2. Change`inputStream` to use `R.raw.fitness_classes_02_ol`
4. Run the application.
5. Open the "Blue" demo.
6. Observe how the `ScheduleRowTrack` displays overlapping data (by just laying out every item end-to-end) vs. the `ScheduleLayoutTrack` (by actually rendering the sessions.
## Overlapping Data with Multiple Tracks and Shared Scroll State

1. Run the application.
2. Open the "Red" demo.
3. Horizontally scroll each individual track and observe scrolling behavior.
4. Vertically scroll to the bottom track and observe relative scroll end vs. top tracks.
## Overlapping Data with Multiple Tracks and Shared Viewport
1. Run the application.
2. Open the "Purple" demo.
3. Horizontally scroll the interface and observe coordinated scrolling and minimum and maximum viewport positions.

# 5. Viewport-based Custom Layout Schedule with Custom Layout Tracks.
## Schedule Viewport Model
Open `rt.yotei.schedule.ui.ScheduleViewport` to see the schedule viewport state object.
## Schedule Scope
### Framework Inspiration: `Box`
1. Open  `androidx.compose.foundation.layout.Box`
2. Look at `inline fun Box` parameters and in particular the `content` parameter with its `BoxScope` receiver in the function type.
3. Look at `interface BoxScope` for example of a layout scope defining custom modifier factory methods.
4. Look at `internal object BoxScopeInstance : BoxScope` for example of a singleton implementation of a stateless scope.

### Framework Inspiration: `BoxWithConstraints`
1. Open `androidx.compose.foundation.layout.BoxWithConstraints`
2. Look at `interface BoxWithConstraintsScope : BoxScope` for example of a layout scope with state, the multiple dimension properties.
3. Look at `private data class BoxWithConstraintsScopeImpl` for example of a class implementation of a stateful scope.

### `ScheduleScope` Interface
1. Open `ScheduleScope.kt`.
2. Note that the `interface ScheduleScope` contains both state and custom modifier factory methods.
3. Note that the `interface ScheduleScope` is annotated with `@Stable`.
#### Code Region: About Stability in Compose
The `About Stability in Compose` code region explains the concept of stability, and stable and unstable types in Compose, and how stable types help with recomposition skipping.

### `ScheduleScope` Implementation
1. Open `ScheduleScopeImpl.kt`
2. Look at the `internal class ScheduleScopeImpl` for implementations of the properties (as state so that they can notify Compose of changes) and the modifier factory methods.
3. Look at the modifier factory, `timeRange`, which produces a `TimeRangeElement` node. This leads to [Custom Modifiers](#custom-modifiers)

### Custom Modifiers
1. Open `ScheduleModifiers.kt`
2. Look at `TimeRangeNode` and `TimeRangeElement` for the basic implementation of a custom modifier.
3. Look at `Measurable.timeRange` and `Placeable.timeRange` for by-convention extension methods for easily accessing the node as `ParentData`.
#### `ParentDataModifier`
Open `androidx.compose.ui.node.ParentDataModifier` to see the `DelegatableNode` interface extension for providing data from a modified element to its parent layout.

### Instantiating `ScheduleScope`
1. Open `ScheduleScopeImpl.kt`.
2. Look at the `rememberScheduleScope` method for a `rememberSaveable` based factory method for our `ScheduleScopeImpl` that is remembered across recompositions and updates the `ScheduleScope.viewport` property without reinitializing the entire scope.
3. Open `Schedule.kt`
4. Go to `@Composable fun Schedule.
5. Look at the first statement for the call to `rememberScheduleScope`.

## Calling Scoped Composables
1. Go to `@Composable fun Schedule`.
2. Go to `content` parameter definition.
3. Look at `scope` instance being used as receiver to the `@Composable ScheduleTrack`.
4. Temporarily delete the `scope` instance to see the `@Composable ScheduleTrack` no longer being in scope, and thus uncallable.

## Calling Scoped Modifiers
1. Go to `@Composable fun ScheduleScope.ScheduleTrack`.
2. If the `Composition` code region is folded, unfold it.
3. Look at the `modifier` parameter of the `@Composable Session` and see the `timeRange` custom modifier method being called.

## Staggering Overlapping Classes
1. Go to `@Composable fun ScheduleScope.ScheduleTrack`.
2. Uncomment code inside of the `But what if overlap?` code region.
3. Comment all of the code inside of the `Measurement` code region.
4. Comment all of the code inside of the `Placement` code region before the second `But what if overlap?` region.
5. Uncomment code inside of the second `But what if overlap?` code region.
6. Run the application.
7. Open the "Purple" demo.
8. Look at the layout of overlapping classes to see that they are not rendered over top of each other.
# 6. Using `LookaheadScope`

## `ApproachLayoutModifierNode` Implementation
1. Open `AnimatedScheduleModifier.kt`.
2. Look at the implementation of a `ApproachLayoutModifierNode` via the `AnimatedPlacementModifierNode` class.
3. Open `TrackScope.kt`.
4. Look at the `animateItemPlacement` modifier factory method in `TrackScope` and in `TrackScopeInstance`.
5. Go to `@Composable fun OpenCustomLayoutScreen`.
6. Look at the `LookaheadScope` that wraps all of the screen content.
7. Look at the usage of the `animateItemPlacement` on the `TrackLabel` and `Session` instances.
