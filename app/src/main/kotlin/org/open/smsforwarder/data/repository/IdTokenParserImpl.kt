package org.open.smsforwarder.data.repository

import android.util.Base64
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import org.open.smsforwarder.domain.IdTokenParser
import javax.inject.Inject

class IdTokenParserImpl @Inject constructor() : IdTokenParser {

    override fun extractEmail(idToken: String): String? {
        val parts = idToken.split(".")
        if (parts.size != ID_TOKEN_PARTS_SIZE) return null

        return try {
            val payload = parts[1]
            val paddedPayload =
                payload.padEnd(
                    (payload.length + PADDING_OFFSET)
                            / BLOCK_SIZE_DIVIDER * BLOCK_SIZE_MULTIPLIER, PAD_CHAR
                )
            val decodedBytes = Base64.decode(paddedPayload, Base64.URL_SAFE or Base64.NO_WRAP)
            val json = JSONObject(String(decodedBytes))
            json.optString(EMAIL)
        } catch (e: JSONException) {
            Log.w("TokenParser", "Failed to parse JSON in ID Token", e)
            null
        } catch (e: IllegalArgumentException) {
            Log.w("TokenParser", "Failed to decode Base64 payload in ID Token", e)
            null
        }
    }

    private companion object {
        const val EMAIL = "email"
        const val PAD_CHAR = '='
        const val ID_TOKEN_PARTS_SIZE = 3
        const val PADDING_OFFSET = 3
        const val BLOCK_SIZE_DIVIDER = 4
        const val BLOCK_SIZE_MULTIPLIER = 4
    }
}
