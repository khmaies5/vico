/*
 * Copyright 2022 Patryk Goworowski and Patryk Michalik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.patrykandpatryk.vico.core.entry

import com.patrykandpatryk.vico.core.chart.Chart

/**
 * The base for single chart entry rendered by [Chart] subclasses.
 * It holds information about x-axis and y-axis location.
 */
public interface ChartEntry {

    /**
     * The position on x-axis of this [ChartEntry].
     */
    public val x: Float

    /**
     * The position on y-axis of this [ChartEntry].
     */
    public val y: Float

    /**
     * @see x
     */
    public operator fun component1(): Float = x

    /**
     * @see y
     */
    public operator fun component2(): Float = y
}
