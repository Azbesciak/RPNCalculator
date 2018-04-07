package com.witkups.rpncalculator.settings

import com.witkups.rpncalculator.R

object ThemeManager {
    const val THEME_PREF_KEY = "app_theme"
    const val DEFAULT_THEME = "0"
    var currentTheme = 0
    fun getTheme(index: String = currentTheme.toString()):Int {
        currentTheme = index.toInt()
        return Theme.values()[currentTheme].themeId
    }
}

enum class Theme(val themeId: Int) {
    Default(R.style.AppTheme),
    Dark(R.style.DarkTheme)
}