package com.prabhas.flightsearchapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferences(context: Context) {

    private val store = context.dataStore

    private val SEARCH_KEY = stringPreferencesKey("search_text")

    val searchText = store.data.map {
        it[SEARCH_KEY] ?: ""
    }

    suspend fun saveSearch(text: String) {
        store.edit {
            it[SEARCH_KEY] = text
        }
    }
}
