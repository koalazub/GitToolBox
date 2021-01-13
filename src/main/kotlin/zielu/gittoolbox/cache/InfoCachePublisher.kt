package zielu.gittoolbox.cache

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import git4idea.repo.GitRepository
import zielu.gittoolbox.cache.PerRepoInfoCache.CACHE_CHANGE
import zielu.gittoolbox.util.LocalGateway

internal class InfoCachePublisher(private val project: Project) : LocalGateway(project) {
  fun notifyEvicted(repositories: Collection<GitRepository>) {
    publishAsync { it.syncPublisher(CACHE_CHANGE).evicted(repositories) }
  }

  fun notifyRepoChanged(repo: GitRepository, previous: RepoInfo, current: RepoInfo) {
    publishAsync {
      it.syncPublisher(CACHE_CHANGE).stateChanged(previous, current, repo)
      log.debug("Published cache changed event: ", repo)
    }
  }

  private companion object {
    private val log = Logger.getInstance(InfoCachePublisher::class.java)
  }
}
