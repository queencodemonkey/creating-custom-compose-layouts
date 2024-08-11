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

//
// Taken from "Now in Android" example app/project from Google:
// https://github.com/android/nowinandroid/blob/main/core/common/src/main/kotlin/com/google/samples/apps/nowinandroid/core/network/di/DispatchersModule.kt
//

package rt.yotei.common.coroutines

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import rt.yotei.common.coroutines.RtDispatchers.Default
import rt.yotei.common.coroutines.RtDispatchers.IO

/**
 * Dagger module that provides Coroutine Dispatchers.
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatcherModules {
  /**
   * Provides the default [CoroutineDispatcher].
   *
   * Returns the default dispatcher which is [Dispatchers.Default],
   * which is optimized for CPU-intensive work off the main thread.
   */
  @Provides
  @Dispatcher(Default)
  fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

  /**
   * Provides a [CoroutineDispatcher] for I/O operations.
   *
   * Returns [Dispatchers.IO], which is optimized for offloading
   * blocking I/O tasks to a shared pool of threads.
   */
  @Provides
  @Dispatcher(IO)
  fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}
