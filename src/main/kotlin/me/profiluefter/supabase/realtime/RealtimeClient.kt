package me.profiluefter.supabase.realtime

import org.phoenixframework.Socket

class RealtimeClient(url: String, private val anonToken: String) {
    var userToken: String? = null

    private val socket by lazy {
        Socket(url, mapOf("apikey" to anonToken), decode = ::decodeRealtimeMessage).apply {
            connect()
        }
    }

    fun <T> subscribe(topic: String): RealtimeChannel<T> {
        val params = if (userToken != null) mapOf("user_token" to userToken) else emptyMap()
        return RealtimeChannel(socket.channel(topic, params))
    }

    fun disconnect(callback: (() -> Unit)? = null) {
        socket.disconnect(callback = callback)
    }
}