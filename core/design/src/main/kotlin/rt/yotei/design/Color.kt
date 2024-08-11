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

package rt.yotei.design

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

//
// CCCL color palette
//

val AndroidGreen = Color(0xFF69F0AE)

val Gray100 = Color(0xFFF5F5F5)
val Gray900 = Color(0xFF212121)

val RedA400 = Color(0xFFFF1744)

val Pink40 = Color(0xFF7D5260)
val Pink80 = Color(0xFFEFB8C8)
val PinkA200 = Color(0xFFFF4081)
val PinkA400 = Color(0xFFF50057)

val Purple40 = Color(0xFF6650a4)
val Purple80 = Color(0xFFD0BCFF)
val PurpleA200 = Color(0xFFE040FB)
val PurpleA700 = Color(0xFFAA00FF)
val DeepPurpleA400 = Color(0xFF651FFF)

val BlueA200 = Color(0xFF448AFF)
val BlueA700 = Color(0xFF2962FF)

val CyanA200 = Color(0xFF18FFFF)

val GreenA200 = Color(0xFF69F0AE)

val LimeA400 = Color(0xFFC6FF00)

val YellowA200 = Color(0xFFFFFF00)

val OrangeA400 = Color(0xFFFFC400)

val DeepOrangeA400 = Color(0xFFFF3D00)

val BlueGray50 = Color(0xFFECEFF1)
val BlueGray100 = Color(0xFFCFD8DC)
val BlueGray200 = Color(0xFFB0BEC5)
val BlueGray400 = Color(0xFF78909C)
val BlueGray900 = Color(0xFF263238)

val PurpleGrey40 = Color(0xFF625b71)
val PurpleGrey80 = Color(0xFFCCC2DC)

val UnlimitedVoid = Brush.linearGradient(
  colorStops = arrayOf(
    0.00f to Color(0xFF08C8EA),
    0.23f to Color(0xFF3FF1FE),
    0.70f to Color(0xFFFFFFFF),
    1.00f to Color(0xFF2278B9),
  ),
  start = Offset.Zero,
  end = Offset(0f, Float.POSITIVE_INFINITY),
)
