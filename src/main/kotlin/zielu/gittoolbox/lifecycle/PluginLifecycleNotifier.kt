package zielu.gittoolbox.lifecycle

import com.intellij.util.messages.Topic

internal interface PluginLifecycleNotifier {
  fun onLoaded()

  companion object {
    val TOPIC = Topic.create("Git ToolBox Lifecycle", PluginLifecycleNotifier::class.java)
  }
}
