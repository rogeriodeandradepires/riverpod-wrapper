package me.pandras.riverpod_wrapper

/**
 * Intention action to wrap a widget with a HookConsumer.
 */
class WrapWithHookConsumerIntentionAction : WrapWithRiverpodIntentionAction(SnippetType.HookConsumer) {
    override fun getText(): String = "Wrap with HookConsumer"
}
