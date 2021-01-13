package zielu.gittoolbox.cache

import com.google.common.cache.Cache
import com.intellij.openapi.Disposable
import com.intellij.openapi.vfs.VirtualFile
import java.util.function.Supplier

internal interface VirtualFileRepoCacheLocalGateway {
  fun fireCacheChanged()

  fun fireAdded(roots: Collection<VirtualFile>)

  fun fireRemoved(roots: Collection<VirtualFile>)

  fun rootsVFileCacheSizeGauge(size: () -> Int)

  fun rootsFilePathCacheSizeGauge(size: () -> Int)

  fun exposeDirsCacheMetrics(cache: Cache<*, *>)

  fun <T> repoForDirCacheTimer(supplier: Supplier<T>): T

  fun disposeWithProject(disposable: Disposable)
}
