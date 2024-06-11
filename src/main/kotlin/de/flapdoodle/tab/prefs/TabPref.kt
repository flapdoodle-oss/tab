package de.flapdoodle.tab.prefs

import java.util.prefs.Preferences

object TabPref {

    data class WindowPosition(val x: Double, val y: Double, val width: Double, val height: Double)

    private fun preferences(): Preferences {
        val prefs = Preferences.userRoot().node("Tab")
        return prefs
    }

    fun windowPosition(): WindowPosition? {
        val prefs = preferences()
        return if (prefs.getDouble("x", -1.0) > 0.0) {
            WindowPosition(
                x = prefs.getDouble("x", 100.0),
                y = prefs.getDouble("y", 100.0),
                width = prefs.getDouble("width", 800.0),
                height = prefs.getDouble("height", 600.0)
            )
        } else {
            null
        }
    }

    fun storeWindowPosition(windowPosition: WindowPosition) {
        val prefs = preferences()
        prefs.putDouble("x", windowPosition.x)
        prefs.putDouble("y", windowPosition.y)
        prefs.putDouble("width", windowPosition.width)
        prefs.putDouble("height", windowPosition.height)
    }
}