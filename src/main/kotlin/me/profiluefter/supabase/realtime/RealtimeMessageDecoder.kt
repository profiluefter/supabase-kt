package me.profiluefter.supabase.realtime

import kotlinx.serialization.json.*
import org.phoenixframework.Message

fun decodeRealtimeMessage(raw: String): Message {
    val rawArray = Json.parseToJsonElement(raw).jsonArray
    return Message(
        joinRef = rawArray[0].jsonString,
        ref = rawArray[1].jsonString ?: "",
        topic = rawArray[2].jsonString ?: "",
        event = rawArray[3].jsonString ?: "",
        rawPayload = Json.decodeFromJsonElement<Map<String, JsonElement>>(rawArray[4]) + mapOf("jsonElement" to rawArray[4])
    )
}

val JsonElement.jsonString
    get() = jsonPrimitive.contentOrNull
