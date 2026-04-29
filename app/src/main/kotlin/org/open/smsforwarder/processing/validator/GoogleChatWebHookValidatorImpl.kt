package org.open.smsforwarder.processing.validator

import androidx.core.net.toUri
import org.open.smsforwarder.domain.GoogleChatWebHookValidator
import javax.inject.Inject

class GoogleChatWebHookValidatorImpl @Inject constructor() : GoogleChatWebHookValidator {

    override fun isValid(webHook: String): Boolean {
        val uri = webHook.trim().toUri()
        val pathSegments = uri.pathSegments
        return uri.scheme == HTTPS_SCHEME &&
                uri.host == GOOGLE_CHAT_HOST &&
                pathSegments.size >= MIN_PATH_SEGMENTS &&
                pathSegments[V1_SEGMENT_INDEX] == V1_SEGMENT &&
                pathSegments[SPACES_SEGMENT_INDEX] == SPACES_SEGMENT &&
                pathSegments[SPACE_ID_SEGMENT_INDEX].isNotBlank() &&
                pathSegments[MESSAGES_SEGMENT_INDEX] == MESSAGES_SEGMENT &&
                !uri.getQueryParameter(KEY_QUERY_PARAM).isNullOrBlank() &&
                !uri.getQueryParameter(TOKEN_QUERY_PARAM).isNullOrBlank()
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
    }
}
