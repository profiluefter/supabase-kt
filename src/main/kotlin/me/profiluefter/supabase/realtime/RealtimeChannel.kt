package me.profiluefter.supabase.realtime

import kotlinx.serialization.json.*
import org.phoenixframework.Channel
import org.phoenixframework.Message
import org.phoenixframework.Payload

class RealtimeChannel<T>(private val channel: Channel) {
    fun join(onError: (Message) -> Unit = {}, onTimeout: () -> Unit = {}, onSuccess: () -> Unit = {}) {
        channel
            .join()
            .receive("error") { onError(it) }
            .receive("timeout") { onTimeout() }
            .receive("ok") { onSuccess() }
    }

    fun onRawEvent(event: String, callback: (Message) -> Unit) = this.apply {
        channel.on(event) {
            callback(it)
        }
    }

    inline fun <reified T2 : T> onInsert(crossinline callback: (RealtimeInsertMessage<T2>) -> Unit) = this.apply {
        onRawEvent("INSERT") {
            callback(parsePayload(it.payload))
        }
    }

    inline fun <reified T2 : T> onUpdate(crossinline callback: (RealtimeUpdateMessage<T2>) -> Unit) = this.apply {
        onRawEvent("UPDATE") {
            callback(parsePayload(it.payload))
        }
    }

    inline fun <reified T2 : T> onDelete(crossinline callback: (RealtimeDeleteMessage<T2>) -> Unit) = this.apply {
        onRawEvent("DELETE") {
            callback(parsePayload(it.payload))
        }
    }
}

val jsonWithUnknownKeys = Json { ignoreUnknownKeys = true }

inline fun <reified T> parsePayload(payload: Payload): T {
    val encoded = payload["jsonElement"] as JsonElement
    return jsonWithUnknownKeys.decodeFromJsonElement(encoded)
}
