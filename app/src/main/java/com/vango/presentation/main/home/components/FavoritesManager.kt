package com.vango.presentation.main.home.components

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vango.shared.dtos.places.PlacesResponseDto

object FavoritesManager {
    private const val PREFS_NAME = "FavoritesPrefs"
    private const val KEY_FAVORITES = "favorite_places"
    private val gson = Gson()

    fun addFavorite(context: Context, place: PlacesResponseDto) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val favorites = getFavorites(context).toMutableList()
        if (!favorites.any { it.placeId == place.placeId }) {
            favorites.add(place)
            prefs.edit().putString(KEY_FAVORITES, gson.toJson(favorites)).apply()
        }
    }

    fun removeFavorite(context: Context, placeId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val favorites = getFavorites(context).toMutableList()
        favorites.removeAll { it.placeId == placeId }
        prefs.edit().putString(KEY_FAVORITES, gson.toJson(favorites)).apply()
    }

    fun getFavorites(context: Context): List<PlacesResponseDto> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_FAVORITES, null) ?: return emptyList()
        val type = object : TypeToken<List<PlacesResponseDto>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun isFavorite(context: Context, placeId: String?): Boolean {
        return getFavorites(context).any { it.placeId == placeId }
    }
}