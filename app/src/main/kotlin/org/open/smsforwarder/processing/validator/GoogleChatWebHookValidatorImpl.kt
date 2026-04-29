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
                pathSegments[0] == V1_SEGMENT &&
                pathSegments[1] == SPACES_SEGMENT &&
                pathSegments[2].isNotBlank() &&
                pathSegments[3] == MESSAGES_SEGMENT &&
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
    }
}
