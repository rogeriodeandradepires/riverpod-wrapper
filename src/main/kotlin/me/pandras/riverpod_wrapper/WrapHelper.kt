package me.pandras.riverpod_wrapper

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

/**
 * Helper object for wrapping widgets.
 */
object WrapHelper {
    /**
     * Finds the call expression for the given PSI element.
     */
    fun callExpressionFinder(psiElement: PsiElement): PsiElement? {
        var psiElementFinder: PsiElement? = psiElement.parent

        for (i in 1..10) {
            if (psiElementFinder == null) {
                return null
            }

            if (psiElementFinder.toString() == "CALL_EXPRESSION") {
                if (psiElementFinder.text.startsWith(psiElement.text)) {
                    return psiElementFinder
                }
                return null
            }
            psiElementFinder = psiElementFinder.parent
        }
        return null
    }

    /**
     * Checks if the selection range is valid.
     */
    fun isSelectionValid(start: Int, end: Int): Boolean {
        if (start <= -1 || end <= -1) {
            return false
        }

        if (start >= end) {
            return false
        }

        return true
    }
}
