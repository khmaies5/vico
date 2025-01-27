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

package com.patrykandpatryk.vico.sample.extensions

import android.content.Context
import android.util.TypedValue
import androidx.annotation.ColorInt
import com.patrykandpatryk.vico.R
import com.patrykandpatryk.vico.core.marker.Marker
import com.patrykandpatryk.vico.sample.component.getMarker

@ColorInt
internal fun Context.resolveColorAttribute(resourceId: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(resourceId, typedValue, true)
    return typedValue.data
}

internal fun Context.getMarker(): Marker = getMarker(
    labelColor = resolveColorAttribute(resourceId = R.attr.colorOnSurface),
    bubbleColor = resolveColorAttribute(resourceId = R.attr.colorSurface),
    indicatorInnerColor = resolveColorAttribute(resourceId = R.attr.colorSurface),
    guidelineColor = resolveColorAttribute(resourceId = R.attr.colorOnBackground),
)
