package com.github.smsforwarder

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.AuthorizationResult
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.open.smsforwarder.platform.CredentialClientWrapper
import org.open.smsforwarder.platform.GoogleAuthClient
import org.open.smsforwarder.platform.GoogleSignInFailure
import org.open.smsforwarder.platform.IdentityClientWrapper

@ExtendWith(MockitoExtension::class)
class GoogleAuthClientTest {

    @Mock
    lateinit var activity: Activity

    @Mock
    lateinit var credentialClientWrapper: CredentialClientWrapper

    @Mock
    lateinit var identityClientWrapper: IdentityClientWrapper

    @Mock
    lateinit var authorizationResult: AuthorizationResult

    @Mock
    lateinit var pendingIntent: PendingIntent

    @Mock
    lateinit var intentSender: IntentSender

    private lateinit var client: GoogleAuthClient

    @BeforeEach
    fun setUp() {
        client = GoogleAuthClient(credentialClientWrapper, identityClientWrapper)
    }

    @Test
    fun `getSignInIntent should succeed`() = runTest {
        whenever(identityClientWrapper.authorize(activity)).thenReturn(authorizationResult)
        whenever(authorizationResult.pendingIntent).thenReturn(pendingIntent)
        whenever(pendingIntent.intentSender).thenReturn(intentSender)

        val result = client.getSignInIntent(activity)

        assertEquals(intentSender, result.intentSender)
        verify(credentialClientWrapper).getCredential(activity)
        verify(identityClientWrapper).authorize(activity)
    }

    @Test
    fun `getSignInIntent should throw MissingPendingIntent when pendingIntent is null`() = runTest {
        whenever(identityClientWrapper.authorize(activity)).thenReturn(authorizationResult)
        whenever(authorizationResult.pendingIntent).thenReturn(null)

        assertThrows<GoogleSignInFailure.MissingPendingIntent> {
            client.getSignInIntent(activity)
        }
        verify(credentialClientWrapper).getCredential(activity)
    }

    @Test
    fun `getSignInIntent should throw CredentialCancellation when cancelled`() = runTest {
        whenever(credentialClientWrapper.getCredential(activity))
            .thenThrow(GoogleSignInFailure.CredentialCancellation)

        assertThrows<GoogleSignInFailure.CredentialCancellation> {
            client.getSignInIntent(activity)
        }
    }

    @Test
    fun `getSignInIntent should throw CredentialsNotFound when credential error occurs`() =
        runTest {
            whenever(credentialClientWrapper.getCredential(activity))
                .thenThrow(GoogleSignInFailure.CredentialsNotFound)

            assertThrows<GoogleSignInFailure.CredentialsNotFound> {
                client.getSignInIntent(activity)
            }
        }

    @Test
    fun `getSignInIntent should throw AuthorizationFailed when authorization fails`() = runTest {
        whenever(credentialClientWrapper.getCredential(activity)).then { /* success */ }
        whenever(identityClientWrapper.authorize(activity))
            .thenThrow(GoogleSignInFailure.AuthorizationFailed)

        assertThrows<GoogleSignInFailure.AuthorizationFailed> {
            client.getSignInIntent(activity)
        }
    }

    @Test
    fun `extractAuthorizationCode should throw AuthResultIntentIsNull when data is null`() {
        assertThrows<GoogleSignInFailure.AuthResultIntentIsNull> {
            client.extractAuthorizationCode(null)
        }
    }

    @Test
    fun `extractAuthorizationCode should throw MissingAuthCode when serverAuthCode is null`() {
        val intent = mock(Intent::class.java)
        whenever(identityClientWrapper.getAuthorizationResultFromIntent(intent))
            .thenReturn(authorizationResult)
        whenever(authorizationResult.serverAuthCode).thenReturn(null)

        assertThrows<GoogleSignInFailure.MissingAuthCode> {
            client.extractAuthorizationCode(intent)
        }
    }

    @Test
    fun `extractAuthorizationCode should return serverAuthCode when present`() {
        val intent = mock(Intent::class.java)
        whenever(identityClientWrapper.getAuthorizationResultFromIntent(intent))
            .thenReturn(authorizationResult)
        whenever(authorizationResult.serverAuthCode).thenReturn("auth-code")

        val code = client.extractAuthorizationCode(intent)

        assertEquals("auth-code", code)
    }
}
