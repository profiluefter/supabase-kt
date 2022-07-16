package me.profiluefter.supabase

import io.supabase.postgrest.PostgrestClient
import io.supabase.postgrest.PostgrestDefaultClient
import me.profiluefter.supabase.gotrue.GoTrueClient
import me.profiluefter.supabase.gotrue.TokenResponse
import me.profiluefter.supabase.gotrue.User
import me.profiluefter.supabase.realtime.RealtimeClient
import java.net.URI

class SupabaseClient(baseURL: String, private val anonToken: String) {
    private val baseURLWithSlash = if (baseURL.endsWith("/")) baseURL else "$baseURL/"

    private var postgrestClient: PostgrestClient = PostgrestDefaultClient(
        URI("${baseURLWithSlash}rest/v1/"),
        mapOf(
            "Authorization" to "Bearer $anonToken",
            "apikey" to anonToken
        )
    )

    private var realtimeClient: RealtimeClient = RealtimeClient(
        "${baseURLWithSlash}realtime/v1/",
        anonToken
    )

    private val authClient: GoTrueClient = GoTrueClient(
        "${baseURLWithSlash}auth/v1/",
        anonToken
    )

    var userToken: String? = null
        set(value) {
            field = value
            postgrestClient = PostgrestDefaultClient(
                URI("${baseURLWithSlash}rest/v1/"),
                mapOf(
                    "Authorization" to "Bearer $value",
                    "apikey" to anonToken
                )
            )
            realtimeClient.userToken = value
            authClient.userToken = value
        }

    var refreshToken: String? = null

    // TODO: handle token expiration

    var user: User? = null
        private set

    fun <T : Any> from(table: String) = postgrestClient.from<T>(table)
    fun <T : Any> rpc(function: String, vararg params: Any?) = postgrestClient.rpc<T>(function, params)

    fun <T> subscribe() = realtimeClient.subscribe<T>("realtime:*")
    fun <T> subscribe(schema: String) = realtimeClient.subscribe<T>("realtime:$schema")
    fun <T> subscribe(schema: String, table: String) = realtimeClient.subscribe<T>("realtime:$schema:$table")
    fun <T> subscribe(schema: String, table: String, column: String, value: Any) =
        realtimeClient.subscribe<T>("realtime:$schema:$table:$column=eq.$value")

    // FIXME: Still stops the JVM from exiting
    // FIXME: Creates a websocket connection when called if there is none already
    fun disconnect(callback: (() -> Unit)? = null) = realtimeClient.disconnect(callback)

    suspend fun signUp(email: String, password: String): TokenResponse =
        authClient.signUpWithEmail(email, password).also {
            userToken = it.accessToken
            refreshToken = it.refreshToken
            user = it.user
        }

    suspend fun signIn(email: String, password: String): TokenResponse =
        authClient.signInWithEmail(email, password).also {
            userToken = it.accessToken
            refreshToken = it.refreshToken
            user = it.user
        }

    suspend fun signOut() {
        try {
            authClient.signOut()
        } finally {
            userToken = null
            refreshToken = null
            user = null
        }
    }
}