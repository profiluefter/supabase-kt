package me.profiluefter.supabase

import io.supabase.postgrest.PostgrestClient
import io.supabase.postgrest.PostgrestDefaultClient
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

    fun <T : Any> from(table: String) = postgrestClient.from<T>(table)
    fun <T : Any> rpc(function: String, vararg params: Any?) = postgrestClient.rpc<T>(function, params)
}