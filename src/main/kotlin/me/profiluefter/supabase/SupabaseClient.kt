package me.profiluefter.supabase

import io.supabase.postgrest.PostgrestClient
import io.supabase.postgrest.PostgrestDefaultClient
import me.profiluefter.supabase.realtime.RealtimeClient
import java.net.URI

class SupabaseClient(baseURL: String, private val token: String) {
    private val baseURLWithSlash = if (baseURL.endsWith("/")) baseURL else "$baseURL/"

    private val postgrestClient: PostgrestClient by lazy {
        PostgrestDefaultClient(
            URI("${baseURLWithSlash}rest/v1/"),
            mapOf(
                "Authorization" to "Bearer $token",
                "apikey" to token
            )
        )
    }

    private val realtimeClient: RealtimeClient by lazy {
        RealtimeClient(
            "${baseURLWithSlash}realtime/v1/websocket",
            mapOf(
                "apikey" to token
            )
        )
    }

    fun <T : Any> from(table: String) = postgrestClient.from<T>(table)
    fun <T : Any> rpc(function: String, vararg params: Any?) = postgrestClient.rpc<T>(function, params)

    fun <T> subscribe() = realtimeClient.subscribe<T>("realtime:*")
    fun <T> subscribe(schema: String) = realtimeClient.subscribe<T>("realtime:$schema")
    fun <T> subscribe(schema: String, table: String) = realtimeClient.subscribe<T>("realtime:$schema:$table")
    fun <T> subscribe(schema: String, table: String, column: String, value: Any) =
        realtimeClient.subscribe<T>("realtime:$schema:$table:$column=eq.$value")
}