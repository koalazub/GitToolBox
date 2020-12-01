package zielu.gittoolbox.config;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import zielu.gittoolbox.store.WorkspaceState;
import zielu.gittoolbox.store.WorkspaceStore;

class ConfigMigrator {
  private final Logger log = Logger.getInstance(getClass());

  boolean migrate(GitToolBoxConfig2 appConfig) {
    boolean migrated = migrateAppV1toV2(appConfig);
    migrated = migrateV2(appConfig) || migrated;
    return migrated;
  }

  boolean migrate(@NotNull Project project,
                  @NotNull GitToolBoxConfigPrj prjConfig,
                  @NotNull GitToolBoxConfig2 appConfig) {
    boolean migrated = migrateProject(project, prjConfig);
    migrated = applyConfigOverrides(project, appConfig, prjConfig) || migrated;
    return migrated;
  }

  private boolean migrateAppV1toV2(@NotNull GitToolBoxConfig2 v2) {
    if (!v2.getPreviousVersionMigrated()) {
      ConfigMigratorV1toV2 migrator = new ConfigMigratorV1toV2();
      migrator.migrate(v2);
      v2.setPreviousVersionMigrated(true);
      log.info("V1 config migrated to V2");
      return true;
    }
    return false;
  }

  private boolean migrateV2(@NotNull GitToolBoxConfig2 config) {
    boolean migrated = false;
    if (config.getVersion() == 1) {
      ConfigMigratorV2.INSTANCE.migrate1To2(config);
      config.setVersion(2);
      log.info("V2 config migrated to version 2");
      migrated = true;
    }
    if (config.getVersion() == 2) {
      ConfigMigratorV2.INSTANCE.migrate2To3(config);
      config.setVersion(3);
      log.info("V2 config migrated to version 3");
      migrated = true;
    }
    return migrated;
  }

  private boolean migrateProject(@NotNull Project project, @NotNull GitToolBoxConfigPrj config) {
    WorkspaceState workspaceState = WorkspaceStore.get(project);
    if (workspaceState.getProjectConfigVersion() == 1) {
      ConfigForProjectMigrator migrator = new ConfigForProjectMigrator(config);
      boolean migrated = migrator.migrate();
      if (migrated) {
        log.info("Project config migrated to version 2");
      }
      workspaceState.setProjectConfigVersion(2);
      log.info("Project config set at version 2");
      return true;
    }
    return false;
  }

  private boolean applyConfigOverrides(@NotNull Project project,
                                       @NotNull GitToolBoxConfig2 appConfig,
                                       @NotNull GitToolBoxConfigPrj prjConfig) {
    if (project.isDefault()) {
      return false;
    }
    ExtrasConfig override = appConfig.getExtrasConfig();
    ConfigOverridesMigrator migrator = new ConfigOverridesMigrator(project, override);
    boolean migrated = migrator.migrate(prjConfig);
    if (migrated) {
      log.info("Project overrides applied");
    }
    return migrated;
  }
}
