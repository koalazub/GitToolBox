package zielu.gittoolbox.ui.projectview

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import zielu.gittoolbox.ui.util.AppUiUtil.invokeLater
import zielu.gittoolbox.util.AppUtil
import zielu.intellij.util.ZDisposeGuard

internal class ProjectViewSubscriber(
  private val project: Project
) : Disposable {
  private val disposeGuard = ZDisposeGuard()
  init {
    Disposer.register(this, disposeGuard)
  }

  fun refreshProjectView() {
    if (disposeGuard.isActive()) {
      invokeLater(disposeGuard, Runnable { refreshProjectViewInternal() })
    }
  }

  private fun refreshProjectViewInternal() {
    log.debug("Refreshing project view")
    if (disposeGuard.isActive()) {
      ProjectView.getInstance(project).refresh()
      log.debug("Project view refreshed")
    }
  }

  override fun dispose() {
    // do nothing
  }

  companion object {
    private val log = Logger.getInstance(ProjectViewSubscriber::class.java)

    fun getInstance(project: Project): ProjectViewSubscriber {
      return AppUtil.getServiceInstance(project, ProjectViewSubscriber::class.java)
    }
  }
}
