package zielu.gittoolbox.blame

import com.codahale.metrics.Timer
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.history.VcsRevisionNumber
import com.intellij.openapi.vfs.VirtualFile
import git4idea.repo.GitRepository
import zielu.gittoolbox.cache.VirtualFileRepoCache
import zielu.gittoolbox.util.ExecutableTask
import zielu.gittoolbox.util.LocalGateway

internal class BlameCacheLocalGateway(private val project: Project) : LocalGateway(project) {
  fun getCacheGetTimer(): Timer = getMetrics().timer("blame-cache.get")

  fun getLoadTimer(): Timer = getMetrics().timer("blame-cache.load")

  fun getQueueWaitTimer(): Timer = getMetrics().timer("blame-cache.queue-wait")

  private val discardedCounter by lazy {
    getMetrics().counter("blame-cache.discarded.count")
  }

  fun fireBlameUpdated(vFile: VirtualFile, annotation: BlameAnnotation) {
    publishAsync { it.syncPublisher(BlameCache.CACHE_UPDATES).cacheUpdated(vFile, annotation) }
  }

  fun fireBlameInvalidated(vFile: VirtualFile) {
    publishAsync { it.syncPublisher(BlameCache.CACHE_UPDATES).invalidated(vFile) }
  }

  fun getRepoForFile(vFile: VirtualFile): GitRepository? {
    return VirtualFileRepoCache.getInstance(project).getRepoForFile(vFile)
  }

  fun getBlameLoader(): BlameLoader {
    return BlameLoader.getInstance(project)
  }

  fun execute(task: ExecutableTask) {
    BlameCacheExecutor.getInstance(project).execute(task)
  }

  fun getCurrentRevision(repository: GitRepository): VcsRevisionNumber {
    return getBlameLoader().getCurrentRevision(repository)
  }

  fun registerQueuedGauge(gauge: () -> Int) {
    getMetrics().gauge("blame-cache.queue.size", gauge)
  }

  fun submitDiscarded() {
    discardedCounter.inc()
  }

  fun invalidateForRoot(root: VirtualFile) {
    BlameLoader.getExistingInstance(project).ifPresent { it.invalidateForRoot(root) }
  }
}
