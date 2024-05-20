package me.pandras.riverpod_wrapper

import com.intellij.ide.plugins.PluginEnabler
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.project.Project

/**
 * Startup activity to enable required plugins for the Riverpod wrapper functionality.
 */
class FlutterPluginEnabler : StartupActivity {
    override fun runActivity(project: Project) {
        val flutterPluginId = PluginId.getId("io.flutter")
        val dartPluginId = PluginId.getId("Dart")
        val androidPluginId = PluginId.getId("org.jetbrains.android")

        enablePlugin(flutterPluginId)
        enablePlugin(dartPluginId)
        enablePlugin(androidPluginId)
    }

    /**
     * Enables the specified plugin if it is currently disabled.
     */
    private fun enablePlugin(pluginId: PluginId) {
        val pluginDescriptor = PluginManagerCore.getPlugin(pluginId)
        if (pluginDescriptor != null && PluginManagerCore.isDisabled(pluginId)) {
            PluginEnabler.getInstance().enableById(setOf(pluginId))
        }
    }
}
