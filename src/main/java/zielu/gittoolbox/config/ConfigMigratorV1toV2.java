package zielu.gittoolbox.config;

import static zielu.gittoolbox.config.DecorationPartType.BRANCH;
import static zielu.gittoolbox.config.DecorationPartType.LOCATION;
import static zielu.gittoolbox.config.DecorationPartType.STATUS;
import static zielu.gittoolbox.config.DecorationPartType.TAGS_ON_HEAD;

import com.intellij.openapi.diagnostic.Logger;
import java.util.ArrayList;
import java.util.List;

class ConfigMigratorV1toV2 {
  private final Logger log = Logger.getInstance(getClass());

  private final GitToolBoxConfig v1;

  ConfigMigratorV1toV2() {
    this(GitToolBoxConfig.getInstance());
  }

  ConfigMigratorV1toV2(GitToolBoxConfig v1) {
    this.v1 = v1;
  }

  void migrate(GitToolBoxConfig2 v2) {
    if (v1.isVanilla()) {
      log.info("V1 config is vanilla, no migration needed");
      return;
    }

    v2.setPresentationMode(v1.presentationMode);
    v2.setUpdateProjectActionId(v1.updateProjectActionId);
    v2.setShowStatusWidget(v1.showStatusWidget);
    v2.setBehindTracker(v1.behindTracker);
    v2.setShowProjectViewStatus(v1.showProjectViewStatus);

    List<DecorationPartConfig> decorationParts = new ArrayList<>();
    decorationParts.add(new DecorationPartConfig(BRANCH));
    decorationParts.add(new DecorationPartConfig(STATUS));
    if (v1.showProjectViewHeadTags) {
      DecorationPartConfig tagsOnHead = DecorationPartConfig.builder()
          .withType(TAGS_ON_HEAD)
          .withPrefix("(")
          .withPostfix(")")
          .build();
      decorationParts.add(tagsOnHead);
    }
    if (v1.showProjectViewLocationPath) {
      DecorationPartConfig.Builder location = DecorationPartConfig.builder().withType(LOCATION);
      if (v1.showProjectViewStatusBeforeLocation) {
        location.withPrefix("- ");
        decorationParts.add(location.build());
      } else {
        decorationParts.add(0, location.build());
      }
    }
    v2.setDecorationParts(decorationParts);
  }
}
