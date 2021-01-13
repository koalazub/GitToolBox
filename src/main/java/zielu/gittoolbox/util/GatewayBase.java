package zielu.gittoolbox.util;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;
import zielu.gittoolbox.metrics.Metrics;
import zielu.gittoolbox.metrics.ProjectMetrics;

public abstract class GatewayBase {
  protected final Project project;

  protected GatewayBase(@NotNull Project project) {
    this.project = project;
  }

  public Metrics metrics() {
    return ProjectMetrics.getInstance(project);
  }

  public void disposeWithProject(@NotNull Disposable disposable) {
    Disposer.register(project, disposable);
  }
}
