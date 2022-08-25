package me.profiluefter.supabase.postgrest

import io.supabase.postgrest.json.PostgrestJsonConverter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.serializer

@OptIn(ExperimentalSerializationApi::class)
class PostgrestJsonConverterKotlinXSerialization : PostgrestJsonConverter {
    override fun <T : Any> deserialize(text: String, responseType: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return Json.decodeFromString(serializer(responseType), text) as T
    }

    override fun <T : Any> deserializeList(text: String, responseType: Class<T>): List<T> {
        @Suppress("UNCHECKED_CAST")
        return Json.decodeFromString(ListSerializer(serializer(responseType)), text) as List<T>
    }

    override fun serialize(data: Any): String {
        // Workaround for SingletonList which is not serializable
        if (Collection::class.java.isAssignableFrom(data::class.java)) {
            val list = ArrayList(data as Collection<*>)
            val serializedObjects = list.map {
                if (it == null)
                    return@map JsonNull
                Json.encodeToJsonElement(serializer(it::class.java), it)
            }
            return Json.encodeToString(serializedObjects)
        }

        // default case
        return Json.encodeToString(serializer(data::class.java), data)
    }
}
