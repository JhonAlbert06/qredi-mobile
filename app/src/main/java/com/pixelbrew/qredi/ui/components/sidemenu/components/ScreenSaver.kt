package com.pixelbrew.qredi.ui.components.sidemenu.components

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope

object ScreenSaver : Saver<Screen, String> {
    override fun restore(value: String): Screen? = Screen.fromRoute(value)
    override fun SaverScope.save(value: Screen): String = value.route
}
