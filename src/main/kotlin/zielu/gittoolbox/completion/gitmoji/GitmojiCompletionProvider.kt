package zielu.gittoolbox.completion.gitmoji

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.TextRange
import zielu.gittoolbox.completion.CompletionProviderBase
import zielu.gittoolbox.completion.CompletionService
import zielu.gittoolbox.config.AppConfig

internal class GitmojiCompletionProvider : CompletionProviderBase() {

  override fun setupCompletions(project: Project, result: CompletionResultSet) {
    if (isEnabled()) {
      val completionService = CompletionService.getInstance(project)
      if (completionService.affected.isNotEmpty()) {
        addCompletions(result)
      }
    }
  }

  private fun isEnabled(): Boolean = AppConfig.get().commitDialogGitmojiCompletion

  private fun addCompletions(result: CompletionResultSet) {
    val insertHandler = PrefixCompletionInsertHandler()
    GitmojiResBundle.keySet().forEach { gitmoji ->
      val description = GitmojiResBundle.message(gitmoji)
      val icon = IconLoader.findIcon("/zielu/gittoolbox/gitmoji/$gitmoji.png", false)
      var builder = LookupElementBuilder.create(":$gitmoji:")
        .withTypeText(description)
        .withIcon(icon)
        .withInsertHandler(insertHandler)

      val wordsList = GitmojiMetadata.getKeywords(gitmoji)
      if (wordsList.isNotEmpty()) {
        builder = builder.withLookupStrings(wordsList)
      }
      result.addElement(builder)
    }
  }
}

private class PrefixCompletionInsertHandler() : InsertHandler<LookupElement> {
  override fun handleInsert(context: InsertionContext, item: LookupElement) {
    val gitmoji = item.lookupString
    var startOffset = context.startOffset - 1
    if (startOffset < 0) {
      startOffset = 0
    }
    val textBeforeOffsets = context.document.getText(TextRange(startOffset, context.tailOffset))
    if (textBeforeOffsets.startsWith(":$gitmoji")) {
      context.document.replaceString(startOffset, context.tailOffset, gitmoji)
    }
  }
}
