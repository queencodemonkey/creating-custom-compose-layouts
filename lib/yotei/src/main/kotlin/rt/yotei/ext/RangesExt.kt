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

package rt.yotei.ext

import rt.yotei.viewport.Viewport


/**
 * Returns the length of this range, or 0 if the range is `null`.
 */
internal val LongRange?.length
  get() = if (this != null) {
    last - first
  } else {
    0
  }

/**
 * Determines if one [LongRange] overlaps another [LongRange].
 * While [contains] looks for whether a range is entirely contained in
 * another, this function looks for whether there is a non-zero
 * range of values that exist in both this range and the [other] range.
 */
fun LongRange.overlaps(other: LongRange?) =
  other != null && ((first >= other.first && start <= other.last) ||
      (other.first in (first + 1)..last))

fun <T : Comparable<T>> ClosedRange<T>.overlaps(other: ClosedRange<T>?) =
  other != null && ((start >= other.start && start <= other.endInclusive) ||
      other.start in this)

fun <T : Comparable<T>, S> ClosedRange<T>.overlaps(viewport: Viewport<T, S>) =
  ((start >= viewport.start && start <= viewport.end) ||
      viewport.start in this)

