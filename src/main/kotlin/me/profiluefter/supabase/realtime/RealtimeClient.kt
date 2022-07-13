package me.profiluefter.supabase.realtime

import org.phoenixframework.Socket

class RealtimeClient(url: String, params: Map<String, String>) {
    private val socket = Socket(url, params, decode = ::decodeRealtimeMessage)

    init {
        socket.connect()
    }

    fun <T> subscribe(topic: String): RealtimeChannel<T> {
        return RealtimeChannel(socket.channel(topic))
    }
}