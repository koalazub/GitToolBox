package zielu.gittoolbox.cache

import com.google.common.cache.Cache
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import zielu.gittoolbox.cache.VirtualFileRepoCache.CACHE_CHANGE
import zielu.gittoolbox.util.LocalGateway
import java.util.function.Supplier

internal class VirtualFileRepoCacheLocalGatewayImpl(
  project: Project
) : LocalGateway(project), VirtualFileRepoCacheLocalGateway {

  override fun fireCacheChanged() {
    publishSync { it.syncPublisher(CACHE_CHANGE).updated() }
  }

  override fun fireAdded(roots: Collection<VirtualFile>) {
    publishSync { it.syncPublisher(CACHE_CHANGE).added(roots) }
  }

  override fun fireRemoved(roots: Collection<VirtualFile>) {
    publishSync { it.syncPublisher(CACHE_CHANGE).removed(roots) }
  }

  override fun rootsVFileCacheSizeGauge(size: () -> Int) {
    getMetrics().gauge("vfile-repo-roots-cache.size", size)
  }

  override fun rootsFilePathCacheSizeGauge(size: () -> Int) {
    getMetrics().gauge("filepath-repo-roots-cache.size", size)
  }

  override fun exposeDirsCacheMetrics(cache: Cache<*, *>) {
    exposeCacheMetrics(cache, "vfile-repo-dirs-cache")
  }

  override fun <T> repoForDirCacheTimer(supplier: Supplier<T>): T {
    return getMetrics().timer("repo-for-dir-cache.load").timeSupplier(supplier)
  }
}
