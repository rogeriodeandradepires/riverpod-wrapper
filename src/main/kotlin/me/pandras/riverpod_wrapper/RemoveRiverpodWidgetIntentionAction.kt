package me.pandras.riverpod_wrapper

/**
 * Intention action to remove Riverpod-related widgets (Consumer, HookConsumer, HookBuilder).
 */
class RemoveRiverpodWidgetIntentionAction : RemoveWidgetIntentionAction(listOf("Consumer", "HookConsumer", "HookBuilder"), "Remove Riverpod Widget")
