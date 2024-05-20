package me.pandras.riverpod_wrapper

/**
 * Utility object containing snippets for wrapping widgets.
 */
object Snippets {
    const val PREFIX_SELECTION = "Subject"

    private const val SUFFIX_CONSUMER = "Consumer"
    private const val SUFFIX_HOOK_CONSUMER = "HookConsumer"

    const val RIVERPOD_SNIPPET_KEY = PREFIX_SELECTION + SUFFIX_CONSUMER

    /**
     * Returns a snippet string based on the snippet type and provided widget text.
     */
    @JvmStatic
    fun getSnippet(snippetType: SnippetType?, riverpodWidget: String, widget: String): String {
        return when (snippetType) {
            SnippetType.Consumer -> consumerSnippet(widget)
            SnippetType.HookConsumer -> hookConsumerSnippet(widget)
            else -> consumerSnippet(widget)
        }
    }

    private fun consumerSnippet(widget: String): String {
        return "Consumer(\n" +
                "  builder: (context, ref, child) {\n" +
                "    return $widget;\n" +
                "  },\n" +
                ")"
    }

    private fun hookConsumerSnippet(widget: String): String {
        return "HookConsumer(\n" +
                "  builder: (context, ref, child) {\n" +
                "    return $widget;\n" +
                "  },\n" +
                ")"
    }
}
