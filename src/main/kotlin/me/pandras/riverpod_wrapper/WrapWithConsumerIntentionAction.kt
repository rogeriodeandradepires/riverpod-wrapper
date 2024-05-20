package me.pandras.riverpod_wrapper

/**
 * Intention action to wrap a widget with a Consumer.
 */
class WrapWithConsumerIntentionAction : WrapWithRiverpodIntentionAction(SnippetType.Consumer) {
    override fun getText(): String = "Wrap with Consumer"
}
