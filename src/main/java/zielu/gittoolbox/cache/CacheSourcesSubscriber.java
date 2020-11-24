package zielu.gittoolbox.cache;

import com.google.common.collect.ImmutableList;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import zielu.gittoolbox.config.GitToolBoxConfigPrj;
import zielu.gittoolbox.util.AppUtil;

class CacheSourcesSubscriber {
  private final Logger log = Logger.getInstance(getClass());
  private final Project project;
  private final List<DirMappingAware> dirMappingAwares = new ArrayList<>();
  private final List<RepoChangeAware> repoChangeAwares = new ArrayList<>();

  CacheSourcesSubscriber(@NotNull Project project) {
    this.project = project;
    dirMappingAwares.add(new LazyDirMappingAware<>(() -> VirtualFileRepoCache.getInstance(project)));
    Supplier<PerRepoInfoCache> infoCacheSupplier = () -> PerRepoInfoCache.getInstance(project);
    dirMappingAwares.add(new LazyDirMappingAware<>(infoCacheSupplier));
    repoChangeAwares.add(new LazyRepoChangeAware<>(infoCacheSupplier));
  }

  static CacheSourcesSubscriber getInstance(@NotNull Project project) {
    return AppUtil.getServiceInstance(project, CacheSourcesSubscriber.class);
  }

  void onRepoChanged(@NotNull GitRepository repository) {
    log.debug("Repo changed: ", repository);
    repoChangeAwares.forEach(aware -> aware.repoChanged(repository));
    log.debug("Repo changed notification done: ", repository);
  }

  void onDirMappingChanged() {
    log.debug("Dir mappings changed");
    GitRepositoryManager gitManager = GitRepositoryManager.getInstance(project);
    ImmutableList<GitRepository> repositories = ImmutableList.copyOf(gitManager.getRepositories());
    dirMappingAwares.forEach(aware -> aware.updatedRepoList(repositories));
    log.debug("Dir mappings change notification done");
  }

  void onConfigChanged(@NotNull GitToolBoxConfigPrj previous, @NotNull GitToolBoxConfigPrj current) {
    if (previous.isReferencePointForStatusChanged(current)) {
      GitRepositoryManager gitManager = GitRepositoryManager.getInstance(project);
      ImmutableList.copyOf(gitManager.getRepositories()).forEach(repo ->
          repoChangeAwares.forEach(aware -> aware.repoChanged(repo)));
    }
  }
}
