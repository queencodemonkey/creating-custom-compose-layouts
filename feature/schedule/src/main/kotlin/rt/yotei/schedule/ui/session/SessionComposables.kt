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

package rt.yotei.schedule.ui.session

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.Dialog
import rt.cccl.schedule.R
import rt.yotei.design.AndroidGreen
import rt.yotei.design.BlueGray900
import rt.yotei.design.DeepPurpleA400
import rt.yotei.design.ElevationTokens
import rt.yotei.design.PinkA400
import rt.yotei.design.PurpleGrey40
import rt.yotei.design.YellowA200
import rt.yotei.model.Session
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

// region // ==== Session Composables ====

/**
 * Preview of the Session Composable.
 */
@Preview(widthDp = 200, heightDp = 200)
@Composable
private fun SessionPreview() {
  Box(
    modifier = Modifier
      .background(BlueGray900)
      .padding(24.dp)
  ) {
    val session = Session(
      name = "Daily Dynamic Ladder Flow",
      category = Session.Category.MindBody,
      startTime = LocalDateTime.parse("2024-09-02T07:00:00"),
      endTime = LocalDateTime.parse("2024-09-02T08:00:00"),
      instructor = "Briohny Smyth",
      location = "Yoga Studio"
    )
    Session(
      session = session, modifier = Modifier.fillMaxSize()
    )
  }
}

@Preview
@Composable
private fun SessionDetailsPreview() {
  val session = Session(
    name = "Full Body Kettlebell Chaos",
    category = Session.Category.Strength,
    startTime = LocalDateTime.parse("2024-09-02T09:30:00"),
    endTime = LocalDateTime.parse("2024-09-02T10:30:00"),
    instructor = "AJ Holland",
    location = "Boathouse"
  )
  SessionDetailsPopup(
    session = session, onDismissRequest = {}, modifier = Modifier
  )
}

/**
 * Display of a [Session] in a schedule.
 *
 * @param session Session data.
 * @param modifier The modifier to be applied to the session.
 * @param colors Colors to apply to this Session Composable as defined by [SessionColors].
 * @param dimens Dimensions to apply to this Session Composable as defined by [SessionDimens].
 * @param backgroundShape Shape for drawing this Session Composables's background.
 */
@Composable
fun Session(
  session: Session,
  modifier: Modifier = Modifier,
  showTimes: Boolean = true,
  onClick: () -> Unit = {},
  colors: SessionColors = SessionDefaults.colors(),
  dimens: SessionDimens = SessionDefaults.dimens(),
  backgroundShape: Shape = SessionDefaults.backgroundShape(dimens.cornerSize),
) {
  Card(
    modifier = modifier,
    onClick = { onClick() },
    colors = CardDefaults.cardColors(
      containerColor = session.containerColor,
      contentColor = session.contentColor,
    ),
    shape = backgroundShape,
    border = BorderStroke(
      width = dimens.borderStrokeWidth,
      color = colors.border,
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = ElevationTokens.Level4),
  ) {
    // Session info text
    Column(modifier = Modifier.padding(16.dp)) {
      if (showTimes) {
        Text(
          text = session.toFormattedTimeString(),
          style = MaterialTheme.typography.titleSmall,
        )
      }
      Text(
        text = session.name,
        style = MaterialTheme.typography.titleMedium
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = session.location,
        style = MaterialTheme.typography.labelLarge,
        lineHeight = 1.5.em,
      )
      Text(
        text = session.instructor,
        style = MaterialTheme.typography.labelSmall,
      )
    }
  }
}

/**
 * Popup displaying the details of a [Session].
 *
 * @param session Session data.
 * @param modifier The modifier to be applied to the popup.
 * @param colors Colors to apply to this Session Composable as defined by [SessionColors].
 * @param dimens Dimensions to apply to this Session Composable as defined by [SessionDimens].
 * @param backgroundShape Shape for drawing this Session Composables's background.
 */
@Composable
fun SessionDetailsPopup(
  session: Session,
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
  colors: SessionColors = SessionDefaults.colors(),
  dimens: SessionDimens = SessionDefaults.dimens(),
  backgroundShape: Shape = SessionDefaults.backgroundShape(dimens.cornerSize),
) {
  Dialog(onDismissRequest = onDismissRequest) {
    Card(
      modifier = modifier,
      colors = CardDefaults.cardColors(
        containerColor = session.containerColor,
        contentColor = session.contentColor,
      ),
      shape = backgroundShape,
      border = BorderStroke(
        width = dimens.borderStrokeWidth,
        color = colors.border,
      ),
    ) {
      Column(
        modifier = Modifier.padding(dimens.padding)
      ) {
        Text(
          text = session.toFormattedTimeString(),
          style = MaterialTheme.typography.labelLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = session.name, style = MaterialTheme.typography.displaySmall)
        Text(text = session.location, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.Bottom,
          horizontalArrangement = Arrangement.End,
        ) {
          Text(
            modifier = Modifier.padding(
              end = dimens.imageSpacing,
              bottom = dimens.imageSpacing
            ),
            text = session.instructor,
            style = MaterialTheme.typography.titleLarge
          )
          val imageResourceId = session.instructorImageResource
          if (imageResourceId != null) {
            Image(
              modifier = Modifier
                .size(dimens.imageSize)
                .clip(CircleShape),
              painter = painterResource(imageResourceId),
              contentScale = ContentScale.FillWidth,
              contentDescription = session.instructor
            )
          }
        }
      }
    }
  }
}

// endregion

// region // ==== Data display helper properties/extensions ====

/**
 * Container/background color to use for displaying a session;
 * based on its category.
 */
internal val Session.containerColor: Color
  get() = when (category) {
    Session.Category.Cardio -> AndroidGreen
    Session.Category.Dance -> PinkA400
    Session.Category.MindBody -> YellowA200
    Session.Category.Strength -> DeepPurpleA400
    Session.Category.MartialArts -> PurpleGrey40
  }

/**
 * Content color primarily used for displaying any text for a session;
 * based on its category/[containerColor].
 */
internal val Session.contentColor: Color
  get() = when (category) {
    Session.Category.Cardio -> BlueGray900
    Session.Category.Dance -> Color.White
    Session.Category.MindBody -> BlueGray900
    Session.Category.Strength -> Color.White
    Session.Category.MartialArts -> Color.White
  }

// Backing date/time formatter
private val SESSION_TIME_FORMATTER: DateTimeFormatter =
  DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

/**
 * Returns formatted date/time string for a session.
 */
private fun Session.toFormattedTimeString() =
  "${SESSION_TIME_FORMATTER.format(startTime)} - ${SESSION_TIME_FORMATTER.format(endTime)}"

/**
 * Returns resource ID for an instructor's avatar or
 * `null` if there is no matching resource.
 *
 * For demo purposes.
 */
private val Session.instructorImageResource: Int?
  get() = when (instructor) {
    "AJ Holland" -> R.drawable.avatar_aj_holland
    "Billy Greer" -> R.drawable.avatar_billy_greer
    "Briohny Smyth" -> R.drawable.avatar_briohny_smyth
    "Corina Lindley" -> R.drawable.avatar_corina_lindley
    "Marguerite Endsley" -> R.drawable.avatar_marguerite_endsley
    "Sam Miller" -> R.drawable.avatar_sam_miller
    "Shane Farmer" -> R.drawable.avatar_shane_farmer
    "Shaun T" -> R.drawable.avatar_shaun_t
    "Vicky Delvaux" -> R.drawable.avatar_vicky_delvaux
    else -> null
  }

// endregion


// region // ==== Session Theming Resources ====

// region // === Theme components ===

/**
 * Represents the container and text colors used in displaying a [Session].
 */
@Immutable
class SessionColors internal constructor(
  val border: Color,
)

/**
 * Represents the various dimensions used in displaying a [Session].
 */
@Immutable
class SessionDimens internal constructor(
  val borderStrokeWidth: Dp,
  val padding: PaddingValues,
  val cornerSize: Dp,
  val imageSize: Dp,
  val imageSpacing: Dp,
)

// endregion

/**
 * Definition of styling defaults for Session Composables.
 */
object SessionDefaults {
  /**
   * Returns [SessionColors] instance with defaults.
   */
  @Composable
  fun colors(
    border: Color = Color.White.copy(alpha = 0.5f),
  ) = SessionColors(
    border = border
  )

  /**
   * Returns [SessionDimens] instance with defaults.
   */
  @Composable
  fun dimens(
    borderStrokeWidth: Dp = 4.dp,
    padding: PaddingValues = PaddingValues(24.dp),
    cornerSize: Dp = 8.dp,
    imageSize: Dp = 64.dp,
    imageSpacing: Dp = 8.dp,
  ) = SessionDimens(
    borderStrokeWidth = borderStrokeWidth,
    padding = padding,
    cornerSize = cornerSize,
    imageSize = imageSize,
    imageSpacing = imageSpacing,
  )

  /**
   * Default background shape for Session Composables.
   */
  val backgroundShape: (Dp) -> Shape = fun(cornerSize: Dp) = RoundedCornerShape(cornerSize)
}

// endregion