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

@file:OptIn(ExperimentalAnimationApi::class)

package rt.cccl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import rt.cccl.Example.CustomLayoutSchedule
import rt.cccl.Example.MultiRowSchedule
import rt.cccl.Example.OpenLayoutSchedule
import rt.cccl.Example.SingleRowSchedule
import rt.cccl.ui.theme.CcclTheme
import rt.yotei.design.BlueA700
import rt.yotei.design.CyanA200
import rt.yotei.design.DeepPurpleA400
import rt.yotei.design.RedA400
import rt.yotei.schedule.ui.demo.CustomLayoutScheduleScreen
import rt.yotei.schedule.ui.demo.MultiTrackRowScheduleScreen
import rt.yotei.schedule.ui.demo.OpenCustomLayoutScreen
import rt.yotei.schedule.ui.demo.SimpleScheduleScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  private val scheduleViewModel: ScheduleViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      CcclTheme {
        MainContent(
          scheduleViewModel = scheduleViewModel,
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}

@Composable
private fun MainContent(
  scheduleViewModel: ScheduleViewModel,
  modifier: Modifier = Modifier,
) {
  var selectedExample by rememberSaveable { mutableStateOf<Example?>(null) }
  AnimatedContent(
    modifier = modifier,
    label = "Screen Transition",
    targetState = selectedExample,
    transitionSpec = {
      if (selectedExample == null) {
        (slideInHorizontally(initialOffsetX = { it }) + fadeIn()) togetherWith slideOutHorizontally(
          targetOffsetX = { -it }) + fadeOut()
      } else {
        (slideInHorizontally(initialOffsetX = { it }) + fadeIn()) togetherWith slideOutHorizontally(
          targetOffsetX = { -it }) + fadeOut()
      }
    }
  ) { targetState ->
    when (targetState) {

      SingleRowSchedule -> {
        val sessions by scheduleViewModel.sessions.collectAsState()
        SimpleScheduleScreen(
          sessions = sessions,
          onBack = { selectedExample = null },
          modifier = Modifier.fillMaxSize()
        )
      }

      MultiRowSchedule -> {
        val sessions by scheduleViewModel.sessions.collectAsState()
        MultiTrackRowScheduleScreen(
          sessions = sessions,
          onBack = { selectedExample = null },
          modifier = Modifier.fillMaxSize()
        )
      }

      CustomLayoutSchedule -> {
        val sessionsByLocation by scheduleViewModel.sessionsByLocation.collectAsState()
        CustomLayoutScheduleScreen(
          sessionsByLocation = sessionsByLocation,
          onBack = { selectedExample = null },
          modifier = Modifier.fillMaxSize()
        )
      }

      OpenLayoutSchedule -> {
        val sessionsByLocation by scheduleViewModel.sessionsByLocation.collectAsState()
        OpenCustomLayoutScreen(
          sessionsByLocation = sessionsByLocation,
          onBack = { selectedExample = null },
          modifier = Modifier.fillMaxSize()
        )
      }

      else -> ExampleSelector(
        onSelectExample = { selectedExample = it },
        modifier = Modifier.fillMaxSize()
      )
    }
  }
}


@Composable
private fun ExampleSelector(
  onSelectExample: (Example) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.padding(horizontal = 24.dp),
    verticalArrangement = Arrangement.Center
  ) {
    for (example in Example.entries) {
      if (!example.enabled) continue
      Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onSelectExample(example) },
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors()
          .copy(
            containerColor = example.containerColor,
            contentColor = example.contentColor
          ),
      ) {
        Text(
          text = example.label,
          style = MaterialTheme.typography.titleMedium
        )
      }
    }
  }
}

private enum class Example(
  val label: String,
  val containerColor: Color,
  val contentColor: Color,
  val enabled: Boolean = true,
) {
  SingleRowSchedule(
    label = "Blue",
    containerColor = BlueA700,
    contentColor = Color.White
  ),
  MultiRowSchedule(
    label = "Red",
    containerColor = RedA400,
    contentColor = Color.White
  ),
  CustomLayoutSchedule(
    label = "Purple",
    containerColor = DeepPurpleA400,
    contentColor = Color.White
  ),
  OpenLayoutSchedule(
    label = "Six Eyes",
    containerColor = CyanA200,
    contentColor = Color.Black
  ),
}
