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

package com.patrykandpatryk.vico.sample.component

import com.patrykandpatryk.vico.core.component.shape.ShapeComponent
import com.patrykandpatryk.vico.core.component.shape.Shapes
import com.patrykandpatryk.vico.core.component.shape.shader.ComponentShader
import com.patrykandpatryk.vico.core.component.shape.shader.DynamicShader

internal fun getDottedShader(dotColor: Int): DynamicShader = ComponentShader(
    componentSizeDp = DOT_SIZE_DP,
    component = ShapeComponent(
        shape = Shapes.pillShape,
        color = dotColor,
    ),
)

private const val DOT_SIZE_DP = 4f
