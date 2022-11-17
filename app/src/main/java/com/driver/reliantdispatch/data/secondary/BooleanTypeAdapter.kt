package com.driver.reliantdispatch.data.secondary

import com.google.gson.*
import java.lang.reflect.Type


internal class BooleanTypeAdapter : JsonDeserializer<Boolean?>, JsonSerializer<Boolean?> {

    override fun serialize(src: Boolean?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(when (src) {
            true -> 1
            false -> 0
            else -> null
        })
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Boolean? {
        return when (json.asInt) {
            1 -> true
            0 -> false
            else -> null
        }
    }
}