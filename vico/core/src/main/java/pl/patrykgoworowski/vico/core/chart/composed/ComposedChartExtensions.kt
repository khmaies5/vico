/*
 * Copyright (c) 2021. Patryk Goworowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.patrykgoworowski.vico.core.chart.composed

import pl.patrykgoworowski.vico.core.entry.ChartEntryModel
import pl.patrykgoworowski.vico.core.chart.Chart

public operator fun <Model : ChartEntryModel> Chart<Model>.plus(
    other: Chart<Model>
): ComposedChart<Model> =
    ComposedChart(listOf(this, other))

public operator fun <Model : ChartEntryModel> ComposedChart<Model>.plus(
    other: Chart<Model>
): ComposedChart<Model> =
    ComposedChart(charts + other)

public operator fun <Model : ChartEntryModel> ComposedChart<Model>.plus(
    other: ComposedChart<Model>
): ComposedChart<Model> =
    ComposedChart(charts + other.charts)