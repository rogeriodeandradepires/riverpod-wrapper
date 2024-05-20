package me.pandras.riverpod_wrapper

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.PsiDocumentManager

/**
 * Base class for intention actions that remove specified widgets and keep their children.
 */
open class RemoveWidgetIntentionAction(private val widgetTypes: List<String>, private val actionText: String) : PsiElementBaseIntentionAction(), IntentionAction {
    var callExpressionElement: PsiElement? = null

    override fun getFamilyName(): String = actionText
    override fun getText(): String = actionText

    override fun isAvailable(project: Project, editor: Editor?, psiElement: PsiElement): Boolean {
        if (editor == null) return false

        val document = editor.document
        val text = document.text
        val caretOffset = editor.caretModel.offset

        // Find the position of the first empty space before the caret
        val spaceBeforeCaret = text.lastIndexOf(' ', caretOffset)
        val widgetText = text.substring(spaceBeforeCaret + 1).trimStart()

        // Check if the widgetText starts with one of our widget types
        val widgetTypesPattern = widgetTypes.joinToString("|") { "\\b$it\\b" }
        val regex = Regex("^($widgetTypesPattern)")
        val matchResult = regex.find(widgetText)

        return matchResult != null
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, element: PsiElement) {
        WriteCommandAction.runWriteCommandAction(project) {
            Common.invokeRemoveWidgetAction(project, editor, widgetTypes)
            // Reformat the code after removing
            reformatCode(project, editor, element)
        }
    }

    override fun startInWriteAction(): Boolean = true

    private fun reformatCode(project: Project, editor: Editor, element: PsiElement) {
        val document = editor.document
        PsiDocumentManager.getInstance(project).commitDocument(document)
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document)
        psiFile?.let {
            CodeStyleManager.getInstance(project).reformatText(psiFile, 0, psiFile.textLength)
        }
    }
}
