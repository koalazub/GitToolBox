package zielu.gittoolbox.lifecycle

import com.intellij.ide.plugins.CannotUnloadPluginException
import com.intellij.ide.plugins.DynamicPluginListener
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.openapi.diagnostic.Logger
import zielu.gittoolbox.GitToolBox

internal class PluginLifecycleListener : DynamicPluginListener {
  override fun pluginLoaded(pluginDescriptor: IdeaPluginDescriptor) {
    if (GitToolBox.isItMe(pluginDescriptor)) {
      log.info("Plugin loaded")
      PluginLifecycleFacade.notifyLoaded()
    }
  }

  override fun beforePluginUnload(pluginDescriptor: IdeaPluginDescriptor, isUpdate: Boolean) {
    if (GitToolBox.isItMe(pluginDescriptor)) {
      log.info("Plugin unloading started")
    }
  }

  override fun pluginUnloaded(pluginDescriptor: IdeaPluginDescriptor, isUpdate: Boolean) {
    if (GitToolBox.isItMe(pluginDescriptor)) {
      log.info("Plugin unloaded")
    }
  }

  override fun checkUnloadPlugin(pluginDescriptor: IdeaPluginDescriptor) {
    if (GitToolBox.isItMe(pluginDescriptor)) {
      log.info("Can unload")
    }
  }

  private companion object {
    private val log = Logger.getInstance(PluginLifecycleListener::class.java)
  }
}
