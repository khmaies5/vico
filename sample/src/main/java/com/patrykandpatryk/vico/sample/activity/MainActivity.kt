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

package com.patrykandpatryk.vico.sample.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.updatePadding
import com.google.android.material.tabs.TabLayoutMediator
import com.patrykandpatryk.vico.databinding.ActivityMainBinding
import com.patrykandpatryk.vico.sample.adapter.TabStateAdapter
import com.patrykandpatryk.vico.sample.extensions.statusBarInsets
import com.patrykandpatryk.vico.sample.util.Tab

internal class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        with(binding) {
            viewpager.adapter = TabStateAdapter(fragmentActivity = this@MainActivity)
            appBarLayout.setOnApplyWindowInsetsListener { view, insets ->
                view.updatePadding(top = insets.statusBarInsets.top)
                insets
            }
            TabLayoutMediator(tabLayout, viewpager) { tab, index ->
                tab.text = getString(Tab.values()[index].labelResourceId)
            }.attach()
        }
    }
}
