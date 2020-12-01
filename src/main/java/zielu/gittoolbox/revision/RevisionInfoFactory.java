package zielu.gittoolbox.revision;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import zielu.gittoolbox.util.AppUtil;

interface RevisionInfoFactory {
  @NotNull
  static RevisionInfoFactory getInstance(@NotNull Project project) {
    return AppUtil.getServiceInstance(project, RevisionInfoFactory.class);
  }

  @NotNull
  RevisionInfo forLine(@NotNull RevisionDataProvider provider, int lineNumber);
}
