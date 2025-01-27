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

package com.patrykandpatryk.vico.core.component.shape.extension

import android.graphics.Path
import com.patrykandpatryk.vico.core.annotation.LongParameterListDrawFunction

/**
 * A convenience function for [Path.cubicTo] that helps with adding a cubic curve with a certain [curvature].
 */
@LongParameterListDrawFunction
public fun Path.horizontalCubicTo(
    prevX: Float,
    prevY: Float,
    x: Float,
    y: Float,
    curvature: Float,
    isLtr: Boolean,
) {
    val layoutDirectionMultiplier = if (isLtr) 1f else -1f
    cubicTo(
        prevX + layoutDirectionMultiplier * curvature, prevY,
        x - layoutDirectionMultiplier * curvature, y,
        x, y,
    )
}
