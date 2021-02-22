package zielu.gittoolbox.lifecycle

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import zielu.gittoolbox.config.AppConfig
import zielu.gittoolbox.config.ProjectConfig

internal class GitToolBoxStartup : StartupActivity, DumbAware {
  override fun runActivity(project: Project) {
    // possibly run config migration
    AppConfig.getConfig()
    ProjectConfig.get(project)

    if (!project.isDefault) {
      // project is ready after migrations are done
      log.info("Project ready")
      GitToolBoxStartupFacade(project).fireProjectReady()
    }
  }

  private companion object {
    private val log = Logger.getInstance(GitToolBoxStartup::class.java)
  }
}
