package com.yjy.data.datastore.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.yjy.data.datastore.api.UserPreferencesDataSource
import javax.inject.Inject

internal class UserPreferencesDataSourceImpl @Inject constructor(
    @UserPreferencesDataStore private val dataStore: DataStore<Preferences>,
) : UserPreferencesDataSource {

}
