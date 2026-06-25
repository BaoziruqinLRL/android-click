package com.example.androidclick.ui.navigation

object Routes {
    const val HOME = "home"
    const val EDITOR = "editor/{scriptId}"
    const val SETTINGS = "settings"

    fun editor(scriptId: Long) = "editor/$scriptId"
}
