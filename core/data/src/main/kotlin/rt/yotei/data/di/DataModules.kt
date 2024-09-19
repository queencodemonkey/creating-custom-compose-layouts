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

package rt.yotei.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import rt.yotei.data.repository.DemoGymDataRepository
import rt.yotei.data.repository.GymDataRepository
import rt.yotei.network.GymDataSource
import rt.yotei.network.demo.DemoGymDataSource
import javax.inject.Singleton

/**
 * Dagger module that provides data repositories.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModules {
  /**
   * Binds [DemoGymDataSource] to the [GymDataSource] interface.
   */
  @Singleton
  @Binds
  internal abstract fun bindDemoGymDataSource(demoGymDataSource: DemoGymDataSource): GymDataSource

  /**
   * Binds [DemoGymDataRepository] to the [GymDataRepository] interface.
   */
  @Singleton
  @Binds
  internal abstract fun bindDemoGymDataRepository(demoGymDataRepository: DemoGymDataRepository): GymDataRepository
}
