package zielu.gittoolbox.branch

import com.intellij.openapi.project.Project
import com.intellij.serviceContainer.NonInjectable
import git4idea.repo.GitRepository
import zielu.gittoolbox.cache.RepoInfo
import zielu.gittoolbox.util.AppUtil

internal class BranchSubscriber
@NonInjectable
constructor(private val gateway: BranchSubscriberLocalGateway) {

  constructor(project: Project) : this(BranchSubscriberLocalGateway(project))

  fun onRepoStateChanged(previous: RepoInfo, current: RepoInfo, repository: GitRepository) {
    if (!previous.isEmpty && !current.isEmpty) {
      if (previous.status().localBranch() != null) {
        if (current.status().localBranch() != null) {
          if (!previous.status().sameLocalBranch(current.status())) {
            gateway.branchSwitch(previous.status().localBranch()!!, current.status().localBranch()!!, repository)
          }
        } else {
          gateway.switchFromBranchToOther(previous.status().localBranch()!!, repository)
        }
      } else if (current.status().localBranch() != null) {
        gateway.switchToBranchFromOther(current.status().localBranch()!!, repository)
      }
    }
  }

  companion object {
    fun getInstance(project: Project): BranchSubscriber {
      return AppUtil.getServiceInstance(project, BranchSubscriber::class.java)
    }
  }
}
