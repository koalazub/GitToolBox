package zielu.gittoolbox.lifecycle

import zielu.gittoolbox.GitToolBoxApp

internal object PluginLifecycleFacade {
  fun notifyLoaded() {
    GitToolBoxApp.getInstance().ifPresent {
      it.publishSync { bus ->
        bus.syncPublisher(PluginLifecycleNotifier.TOPIC).onLoaded()
      }
    }
  }
}
