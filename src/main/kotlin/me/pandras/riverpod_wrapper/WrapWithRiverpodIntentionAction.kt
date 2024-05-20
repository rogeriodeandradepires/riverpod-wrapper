package me.pandras.riverpod_wrapper

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.IncorrectOperationException

/**
 * Base class for intention actions that wrap widgets with Riverpod-related widgets.
 */
abstract class WrapWithRiverpodIntentionAction(private val snippetType: SnippetType) : PsiElementBaseIntentionAction(), IntentionAction {
    var callExpressionElement: PsiElement? = null

    override fun getFamilyName(): String = text

    override fun isAvailable(project: Project, editor: Editor?, psiElement: PsiElement): Boolean {
        val shouldDisplay = Common.shouldDisplayWrapMenu(editor, project, psiElement)
        if (shouldDisplay) {
            callExpressionElement = WrapHelper.callExpressionFinder(psiElement)
            return callExpressionElement != null
        }
        return false
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, element: PsiElement) {
        WriteCommandAction.runWriteCommandAction(project) {
            Common.invokeSnippetAction(project, editor, snippetType, callExpressionElement!!)
        }
    }

    override fun startInWriteAction(): Boolean = true

    override fun getElementToMakeWritable(file: PsiFile): PsiElement? {
        return null
    }
}
