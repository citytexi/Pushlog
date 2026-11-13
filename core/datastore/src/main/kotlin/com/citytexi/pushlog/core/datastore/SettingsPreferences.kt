package com.citytexi.pushlog.core.datastore

import javax.inject.Qualifier

/** The plain `settings` DataStore behind [SettingsDataSource]. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SettingsPreferences
