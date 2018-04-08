package com.witkups.rpncalculator.settings

import android.app.Activity
import android.content.SharedPreferences
import android.preference.PreferenceManager.getDefaultSharedPreferences
import com.witkups.rpncalculator.R

object ThemeManager {
    const val THEME_PREF_KEY = "app_theme"
    const val DEFAULT_THEME = "0"
    var currentTheme = 0
    fun updateTheme(index: String = currentTheme.toString()):Theme {
        currentTheme = index.toInt()
        return Theme.values()[currentTheme]
    }
}

fun Activity.updateTheme(
        actionbar: Boolean,
        preferences: SharedPreferences = getDefaultSharedPreferences(applicationContext),
                         theme: String = preferences.getString(ThemeManager.THEME_PREF_KEY, ThemeManager.DEFAULT_THEME)) {
    setTheme(ThemeManager.updateTheme(theme).let { if (actionbar) it.themeId else it.noActionBar })
}

fun Activity.updateThemeWithRecreate(actionbar: Boolean,
        preferences: SharedPreferences = getDefaultSharedPreferences(applicationContext),
        theme: String = preferences.getString(ThemeManager.THEME_PREF_KEY, ThemeManager.DEFAULT_THEME)) {
    updateTheme(actionbar, preferences, theme)
    recreate()
}
enum class Theme(val themeId: Int, val noActionBar: Int) {
    Default(R.style.AppTheme, R.style.AppTheme_NoActionBar),
    Dark(R.style.DarkTheme, R.style.DarkTheme_NoActionBar)
}