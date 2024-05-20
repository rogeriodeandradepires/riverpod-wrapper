package me.pandras.riverpod_wrapper

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import com.jetbrains.lang.dart.psi.DartClass
import com.jetbrains.lang.dart.psi.DartMethodDeclaration

/**
 * Common utility class containing helper methods for handling intention actions.
 */
class Common {
    companion object {
        /**
         * Checks if the given PsiElement is a ConsumerStatefulWidget.
         */
        fun isConsumerStatefulWidget(psiElement: PsiElement): Boolean {
            return psiElement.text.contains("ConsumerStatefulWidget")
        }

        /**
         * Checks if the given PsiElement is a ConsumerWidget.
         */

        /**
         * Checks if the given PsiElement is a HookConsumerWidget.
         */
        fun isConsumerWidget(psiElement: PsiElement): Boolean {
            return psiElement.text.equals("ConsumerWidget")
        }

        /**
         * Checks if the given PsiElement is a StatefulHookConsumerWidget.
         */
        fun isStatefulHookConsumerWidget(psiElement: PsiElement): Boolean {
            return psiElement.text.contains("StatefulHookConsumerWidget")
        }

        fun isHookConsumerWidget(psiElement: PsiElement): Boolean {
            return psiElement.text.equals("HookConsumerWidget")
        }

        /**
         * Converts a ConsumerWidget to a ConsumerStatefulWidget.
         */
        fun convertConsumerWidgetToConsumerStatefulWidget(project: Project, editor: Editor, element: PsiElement) {
            val document = editor.document

            // Find the class element
            val classElement = PsiTreeUtil.getParentOfType(element, DartClass::class.java) ?: return
            val className = classElement.name ?: return

            // Find the constructor or add a default one if not present
            val constructor = classElement.constructors.firstOrNull()?.text ?: "  const $className({super.key});\n"

            // Find the build method in the current class
            val buildMethod = classElement.methods.find { it.name == "build" } as? DartMethodDeclaration

            val newClassText = StringBuilder().apply {
                append("class $className extends ConsumerStatefulWidget {\n")
                append(constructor)
                append("  @override\n")
                append("  ${className}State createState() => ${className}State();\n")
                append("}\n\n")
                append("class ${className}State extends ConsumerState<$className> {\n")
                append("  @override\n")
                if (buildMethod != null) {
                    val buildMethodText = buildMethod.text.replace("Widget build(BuildContext context, WidgetRef ref)", "Widget build(BuildContext context)")
                    append(buildMethodText.replace("@override", "").trim()) // Remove existing @override annotations
                } else {
                    append("  Widget build(BuildContext context) {\n")
                    append("    return Container();\n")
                    append("  }\n")
                }
                append("}\n")
            }.toString()

            WriteCommandAction.runWriteCommandAction(project) {
                // Replace the text range of the current class with the new class text
                document.replaceString(classElement.textRange.startOffset, classElement.textRange.endOffset, newClassText)
                PsiDocumentManager.getInstance(project).commitDocument(document)

                // Reformat the newly added code
                val psiFileUpdated = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return@runWriteCommandAction
                CodeStyleManager.getInstance(project).reformat(psiFileUpdated)
            }
        }

        /**
         * Converts a ConsumerStatefulWidget to a ConsumerWidget.
         */
        fun convertConsumerStatefulWidgetToConsumerWidget(project: Project, editor: Editor, element: PsiElement) {
            val document = editor.document
            val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return

            // Find the class element
            val classElement = PsiTreeUtil.getParentOfType(element, DartClass::class.java) ?: return
            val className = classElement.name ?: return

            // Find the state class
            val stateClass = psiFile.children
                .filterIsInstance<DartClass>()
                .firstOrNull { it.name?.contains(className + "State") == true } ?: return

            // Find the build method in the state class
            val buildMethod = stateClass.methods.find { it.name == "build" } as? DartMethodDeclaration

            // Find the constructor or add a default one if not present
            val constructor = classElement.constructors.firstOrNull()?.text ?: "  const $className({super.key});\n"

            val newClassText = StringBuilder().apply {
                append("class $className extends ConsumerWidget {\n")
                append(constructor)
                append("  @override\n")
                if (buildMethod != null) {
                    val buildMethodText = buildMethod.text.replace("Widget build(BuildContext context)", "Widget build(BuildContext context, WidgetRef ref)")
                    append(buildMethodText.replace("@override", "").trim()) // Remove existing @override annotations
                } else {
                    append("  Widget build(BuildContext context, WidgetRef ref) {\n")
                    append("    return Container();\n")
                    append("  }\n")
                }
                append("}\n")
            }.toString()

            WriteCommandAction.runWriteCommandAction(project) {
                // Remove the original class and state class
                val classTextRange = classElement.textRange
                val stateClassTextRange = stateClass.textRange
                document.deleteString(classTextRange.startOffset, stateClassTextRange.endOffset)

                // Insert the new class text
                document.insertString(classTextRange.startOffset, newClassText)
                PsiDocumentManager.getInstance(project).commitDocument(document)

                // Reformat the newly added code
                val psiFileUpdated = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return@runWriteCommandAction
                CodeStyleManager.getInstance(project).reformat(psiFileUpdated)
            }
        }

        /**
         * Converts a HookConsumerWidget to a StatefulHookConsumerWidget.
         */
        fun convertHookConsumerWidgetToStatefulHookConsumerWidget(project: Project, editor: Editor, element: PsiElement) {
            val document = editor.document

            // Find the class element
            val classElement = PsiTreeUtil.getParentOfType(element, DartClass::class.java) ?: return
            val className = classElement.name ?: return

            // Find the constructor or add a default one if not present
            val constructor = classElement.constructors.firstOrNull()?.text ?: "  const $className({super.key});\n"

            // Find the build method in the current class
            val buildMethod = classElement.methods.find { it.name == "build" } as? DartMethodDeclaration

            val newClassText = StringBuilder().apply {
                append("class $className extends StatefulHookConsumerWidget {\n")
                append(constructor)
                append("  @override\n")
                append("  ${className}State createState() => ${className}State();\n")
                append("}\n\n")
                append("class ${className}State extends ConsumerState<$className> {\n")
                append("  @override\n")
                if (buildMethod != null) {
                    val buildMethodText = buildMethod.text.replace("Widget build(BuildContext context, WidgetRef ref)", "Widget build(BuildContext context)")
                    append(buildMethodText.replace("@override", "").trim()) // Remove existing @override annotations
                } else {
                    append("  Widget build(BuildContext context) {\n")
                    append("    return Container();\n")
                    append("  }\n")
                }
                append("}\n")
            }.toString()

            WriteCommandAction.runWriteCommandAction(project) {
                // Replace the text range of the current class with the new class text
                document.replaceString(classElement.textRange.startOffset, classElement.textRange.endOffset, newClassText)
                PsiDocumentManager.getInstance(project).commitDocument(document)

                // Reformat the newly added code
                val psiFileUpdated = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return@runWriteCommandAction
                CodeStyleManager.getInstance(project).reformat(psiFileUpdated)
            }
        }

        /**
         * Converts a StatefulHookConsumerWidget to a HookConsumerWidget.
         */
        fun convertStatefulHookConsumerWidgetToHookConsumerWidget(project: Project, editor: Editor, element: PsiElement) {
            val document = editor.document
            val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return

            // Find the class element
            val classElement = PsiTreeUtil.getParentOfType(element, DartClass::class.java) ?: return
            val className = classElement.name ?: return

            // Find the state class
            val stateClass = psiFile.children
                .filterIsInstance<DartClass>()
                .firstOrNull { it.name?.contains(className + "State") == true } ?: return

            // Find the build method in the state class
            val buildMethod = stateClass.methods.find { it.name == "build" } as? DartMethodDeclaration

            // Find the constructor or add a default one if not present
            val constructor = classElement.constructors.firstOrNull()?.text ?: "  const $className({super.key});\n"

            val newClassText = StringBuilder().apply {
                append("class $className extends HookConsumerWidget {\n")
                append(constructor)
                append("  @override\n")
                if (buildMethod != null) {
                    val buildMethodText = buildMethod.text.replace("Widget build(BuildContext context)", "Widget build(BuildContext context, WidgetRef ref)")
                    append(buildMethodText.replace("@override", "").trim()) // Remove existing @override annotations
                } else {
                    append("  Widget build(BuildContext context, WidgetRef ref) {\n")
                    append("    return Container();\n")
                    append("  }\n")
                }
                append("}\n")
            }.toString()

            WriteCommandAction.runWriteCommandAction(project) {
                // Remove the original class and state class
                val classTextRange = classElement.textRange
                val stateClassTextRange = stateClass.textRange
                document.deleteString(classTextRange.startOffset, stateClassTextRange.endOffset)

                // Insert the new class text
                document.insertString(classTextRange.startOffset, newClassText)
                PsiDocumentManager.getInstance(project).commitDocument(document)

                // Reformat the newly added code
                val psiFileUpdated = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return@runWriteCommandAction
                CodeStyleManager.getInstance(project).reformat(psiFileUpdated)
            }
        }

        /**
         * Checks if the wrap menu should be displayed based on the current editor state.
         */
        fun shouldDisplayWrapMenu(editor: Editor?, project: Project, psiElement: PsiElement): Boolean {
            if (editor == null) {
                return false
            }
            val currentFile = getCurrentFile(project, editor)
            if (currentFile != null && !currentFile.name.endsWith(".dart")) {
                return false
            }
            if (psiElement.toString() != "PsiElement(IDENTIFIER)") {
                return false
            }
            return true
        }

        /**
         * Gets the current file being edited.
         */
        fun getCurrentFile(project: Project, editor: Editor): PsiFile? =
            PsiDocumentManager.getInstance(project).getPsiFile(editor.document)

        /**
         * Invokes the action to wrap a selected widget with a snippet.
         */
        fun invokeSnippetAction(project: Project, editor: Editor, snippetType: SnippetType?, callExpressionElement: PsiElement) {
            val document = editor.document
            val elementSelectionRange = callExpressionElement.textRange
            val offsetStart = elementSelectionRange.startOffset
            val offsetEnd = elementSelectionRange.endOffset
            if (!WrapHelper.isSelectionValid(offsetStart, offsetEnd)) {
                return
            }
            val selectedText = document.getText(TextRange.create(offsetStart, offsetEnd))

            val replaceWith = Snippets.getSnippet(snippetType, "", selectedText)

            // wrap the widget:
            WriteCommandAction.runWriteCommandAction(project) {
                document.replaceString(offsetStart, offsetEnd, replaceWith)
            }

            // place cursors to specify types:
            val prefixSelection = Snippets.PREFIX_SELECTION
            val snippetArr = arrayOf(Snippets.RIVERPOD_SNIPPET_KEY)
            val caretModel = editor.caretModel
            caretModel.removeSecondaryCarets()
            for (snippet in snippetArr) {
                if (!replaceWith.contains(snippet)) {
                    continue
                }
                val caretOffset = offsetStart + replaceWith.indexOf(snippet)
                val visualPos = editor.offsetToVisualPosition(caretOffset)
                caretModel.addCaret(visualPos)

                // select snippet prefix keys:
                val currentCaret = caretModel.currentCaret
                currentCaret.setSelection(caretOffset, caretOffset + prefixSelection.length)
            }
            val initialCaret = caretModel.allCarets[0]
            if (!initialCaret.hasSelection()) {
                // initial position from where was triggered the intention action
                caretModel.removeCaret(initialCaret)
            }

            // reformat file:
            ApplicationManager.getApplication().runWriteAction {
                PsiDocumentManager.getInstance(project).commitDocument(document)
                val currentFile = getCurrentFile(project, editor)
                if (currentFile != null) {
                    val unformattedText = document.text
                    val unformattedLineCount = document.lineCount
                    CodeStyleManager.getInstance(project).reformat(currentFile)
                    val formattedLineCount = document.lineCount

                    // file was incorrectly formatted, revert formatting
                    if (formattedLineCount > unformattedLineCount + 3) {
                        document.setText(unformattedText)
                        PsiDocumentManager.getInstance(project).commitDocument(document)
                    }
                }
            }
        }

        /**
         * Checks if the remove menu should be displayed based on the current editor state and the widget types.
         */
        fun shouldDisplayRemoveMenu(editor: Editor?, psiElement: PsiElement, widgetTypes: List<String>): Boolean {
            if (editor == null) return false

            // Traverse up the PSI tree to find if any of the specified widget types are present
            var element: PsiElement? = psiElement
            while (element != null) {
                for (widgetType in widgetTypes) {
                    if (element.text.contains(widgetType)) {
                        return true
                    }
                }
                element = element.parent
            }
            return false
        }

        /**
         * Invokes the action to remove a specified widget and keep its child.
         */
        fun invokeRemoveWidgetAction(project: Project, editor: Editor, widgetTypes: List<String>) {
            val document = editor.document
            val text = document.text
            val caretOffset = editor.caretModel.offset

            // Find the position of the first empty space before the caret
            val spaceBeforeCaret = text.lastIndexOf(' ', caretOffset)

            // Find the widget text after the space
            val widgetText = text.substring(spaceBeforeCaret + 1).trimStart()

            // Check if the widgetText starts with one of our widget types
            val widgetTypesPattern = widgetTypes.joinToString("|") { "\\b$it\\b" }
            val regex = Regex("^($widgetTypesPattern)")
            val matchResult = regex.find(widgetText)

            if (matchResult != null) {
                // Find the start of the current widget by identifying the matching open parenthesis
                val openParenthesisIndex = widgetText.indexOf('(')

                if (openParenthesisIndex != -1) {
                    var closeParenthesisIndex = openParenthesisIndex
                    var openCount = 1
                    while (openCount > 0 && closeParenthesisIndex < widgetText.length - 1) {
                        closeParenthesisIndex++
                        if (widgetText[closeParenthesisIndex] == '(') openCount++
                        if (widgetText[closeParenthesisIndex] == ')') openCount--
                    }

                    // Extract the builder content within the widget
                    val builderContent = widgetText.substring(openParenthesisIndex + 1, closeParenthesisIndex).trim()

                    // Find the return statement and its child content
                    val returnRegex = Regex("return\\s+([^;]+);")
                    val returnMatch = returnRegex.find(builderContent)
                    if (returnMatch != null) {
                        val childContent = returnMatch.groups[1]?.value?.trim() ?: ""

                        // Calculate the range to replace in the document
                        val replaceStart = spaceBeforeCaret + 1
                        val replaceEnd = spaceBeforeCaret + 1 + closeParenthesisIndex + 1

                        val newText = text.substring(0, replaceStart) + childContent + text.substring(replaceEnd)

                        // Apply the new text to the document
                        WriteCommandAction.runWriteCommandAction(project) {
                            document.setText(newText)
                        }
                    }
                }
            }
        }
    }
}
