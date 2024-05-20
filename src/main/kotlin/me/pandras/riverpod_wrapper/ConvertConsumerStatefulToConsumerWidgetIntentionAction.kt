package me.pandras.riverpod_wrapper

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException

/**
 * Intention action to convert a ConsumerStatefulWidget to a ConsumerWidget.
 */
class ConvertConsumerStatefulToConsumerWidgetIntentionAction : PsiElementBaseIntentionAction(), IntentionAction {

    /**
     * Gets the text to display in the intention action list.
     */
    override fun getText(): String = "Convert ConsumerStatefulWidget to ConsumerWidget"

    /**
     * Gets the family name for this intention action.
     */
    override fun getFamilyName(): String = text

    /**
     * Checks if the intention action is available for the given PsiElement.
     */
    override fun isAvailable(project: Project, editor: Editor?, psiElement: PsiElement): Boolean {
        // Check if the psiElement is a ConsumerStatefulWidget
        return Common.isConsumerStatefulWidget(psiElement)
    }

    /**
     * Invokes the intention action to perform the conversion.
     */
    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, element: PsiElement) {
        WriteCommandAction.runWriteCommandAction(project) {
            Common.convertConsumerStatefulWidgetToConsumerWidget(project, editor, element)
        }
    }

    /**
     * Indicates that this intention action starts in a write action.
     */
    override fun startInWriteAction(): Boolean = true
}
