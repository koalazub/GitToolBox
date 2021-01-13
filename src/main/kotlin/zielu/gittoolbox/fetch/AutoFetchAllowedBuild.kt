package zielu.gittoolbox.fetch

import com.intellij.openapi.project.Project
import org.slf4j.LoggerFactory
import zielu.gittoolbox.util.AppUtil
import java.util.concurrent.atomic.AtomicBoolean

internal class AutoFetchAllowedBuild(private val project: Project) {
  private val gateway = AutoFetchAllowedLocalGateway(project)
  private val buildRunning = AtomicBoolean()

  fun isFetchAllowed(): Boolean {
    return !buildRunning.get()
  }

  fun onBuildStarted(builtProject: Project) {
    log.debug("Build started")
    if (isCurrentProject(builtProject)) {
      buildRunning.set(true)
    }
  }

  fun onBuildFinished(builtProject: Project) {
    log.debug("Build finished")
    if (isCurrentProject(builtProject)) {
      if (buildRunning.compareAndSet(true, false)) {
        gateway.fireStateChanged()
      }
    }
  }

  private fun isCurrentProject(builtProject: Project): Boolean {
    return project == builtProject
  }

  companion object {
    private val log = LoggerFactory.getLogger(AutoFetchAllowedBuild::class.java)

    fun getInstance(project: Project): AutoFetchAllowedBuild {
      return AppUtil.getServiceInstance(project, AutoFetchAllowedBuild::class.java)
    }
  }
}
