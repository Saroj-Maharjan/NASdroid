package com.nasdroid.apps.ui

import com.nasdroid.apps.logic.AppsLogicModule
import com.nasdroid.apps.ui.discover.DiscoverAppsViewModel
import com.nasdroid.apps.ui.discover.details.AvailableAppDetailsViewModel
import com.nasdroid.apps.ui.installed.details.InstalledAppDetailsViewModel
import com.nasdroid.apps.ui.installed.overview.InstalledAppsOverviewViewModel
import com.nasdroid.apps.ui.installed.overview.logs.LogsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * A Koin module to inject the apps dependency graph.
 */
val AppsModule = module {
    includes(AppsLogicModule)

    viewModelOf(::AvailableAppDetailsViewModel)
    viewModelOf(::DiscoverAppsViewModel)

    viewModelOf(::InstalledAppDetailsViewModel)

    viewModelOf(::InstalledAppsOverviewViewModel)
    viewModelOf(::LogsViewModel)
}
