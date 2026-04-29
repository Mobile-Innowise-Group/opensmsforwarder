package org.open.smsforwarder.processing.validator

import org.open.smsforwarder.domain.GoogleChatWebHookValidator
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class GoogleChatWebHookValidatorImpl @Inject constructor() : GoogleChatWebHookValidator {

    override fun isValid(webHook: String): Boolean {
        val uri = runCatching { URI(webHook.trim()) }.getOrNull() ?: return false
        val pathSegments = uri.path.trim('/').split('/').filter { it.isNotBlank() }
        val queryParams = parseQueryParams(uri.query)
        return uri.scheme == HTTPS_SCHEME &&
            uri.host == GOOGLE_CHAT_HOST &&
            pathSegments.size >= MIN_PATH_SEGMENTS &&
            pathSegments[V1_SEGMENT_INDEX] == V1_SEGMENT &&
            pathSegments[SPACES_SEGMENT_INDEX] == SPACES_SEGMENT &&
            pathSegments[SPACE_ID_SEGMENT_INDEX].isNotBlank() &&
            pathSegments[MESSAGES_SEGMENT_INDEX] == MESSAGES_SEGMENT &&
            !queryParams[KEY_QUERY_PARAM].isNullOrBlank() &&
            !queryParams[TOKEN_QUERY_PARAM].isNullOrBlank()
    }

    private fun parseQueryParams(query: String?): Map<String, String> {
        if (query.isNullOrBlank()) return emptyMap()
        return query.split(QUERY_PARAMS_SEPARATOR).associate { param ->
            val key = URLDecoder.decode(
                param.substringBefore(QUERY_KEY_VALUE_SEPARATOR),
                StandardCharsets.UTF_8.name()
            )
            val value = URLDecoder.decode(
                param.substringAfter(QUERY_KEY_VALUE_SEPARATOR, ""),
                StandardCharsets.UTF_8.name()
            )
            key to value
        }
    }

    private companion object {
        const val HTTPS_SCHEME = "https"
        const val GOOGLE_CHAT_HOST = "chat.googleapis.com"
        const val V1_SEGMENT = "v1"
        const val SPACES_SEGMENT = "spaces"
        const val MESSAGES_SEGMENT = "messages"
        const val KEY_QUERY_PARAM = "key"
        const val TOKEN_QUERY_PARAM = "token"
        const val MIN_PATH_SEGMENTS = 4
        const val V1_SEGMENT_INDEX = 0
        const val SPACES_SEGMENT_INDEX = 1
        const val SPACE_ID_SEGMENT_INDEX = 2
        const val MESSAGES_SEGMENT_INDEX = 3
        const val QUERY_PARAMS_SEPARATOR = "&"
        const val QUERY_KEY_VALUE_SEPARATOR = "="
    }
}
