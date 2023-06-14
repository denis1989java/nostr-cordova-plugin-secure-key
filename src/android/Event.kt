package com.nostr.band;

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import org.spongycastle.util.encoders.Hex
import java.lang.reflect.Type
import java.security.MessageDigest
import java.util.*


class Event(
    val id: ByteArray,
    @SerializedName("pubkey") val pubKey: ByteArray,
    @SerializedName("created_at") val createdAt: Long,
    val kind: Int,
    val tags: List<List<String>>,
    val content: String,
    val sig: ByteArray,
    val gson: Gson,
    val sha256: MessageDigest
) {
    var generatedId: ByteArray? = null 
    
    init {
        generatedId = generateId(gson, sha256)
    }

    fun generateId(gson: Gson, sha256: MessageDigest): ByteArray {
        val rawEvent = listOf(0, pubKey, createdAt, kind, tags, content)
        val rawEventJson = gson.toJson(rawEvent)
        return sha256.digest(rawEventJson.toByteArray())
    }
}


//________________________________________________________
open class Event(
        val id: ByteArray,
        @SerializedName("pubkey") val pubKey: ByteArray,
        @SerializedName("created_at") val createdAt: Long,
        val kind: Int,
        val tags: List<List<String>>,
        val content: String,
        val sig: ByteArray) {

    class EventDeserializer : JsonDeserializer<Event> {
        override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Event {
            val jsonObject = json.asJsonObject
            return Event(
                    id = Hex.decode(jsonObject.get("id").asString),
                    pubKey = Hex.decode(jsonObject.get("pubkey").asString),
                    createdAt = jsonObject.get("created_at").asLong,
                    kind = jsonObject.get("kind").asInt,
                    tags = jsonObject.get("tags").asJsonArray.map {
                        it.asJsonArray.map { s -> s.asString }
                    },
                    content = jsonObject.get("content").asString,
                    sig = Hex.decode(jsonObject.get("sig").asString))
        }
    }

    class EventSerializer : JsonSerializer<Event> {
        override fun serialize(src: Event, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            return JsonObject().apply {
                addProperty("id", src.id.toHex())
                addProperty("pubkey", src.pubKey.toHex())
                addProperty("created_at", src.createdAt)
                addProperty("kind", src.kind)
                add("tags", JsonArray().also { jsonTags ->
                    src.tags.forEach { tag ->
                        jsonTags.add(JsonArray().also { jsonTagElement ->
                            tag.forEach { tagElement ->
                                jsonTagElement.add(tagElement)
                            }
                        })
                    }
                })
                addProperty("content", src.content)
                addProperty("sig", src.sig.toHex())
            }
        }
    }

    class ByteArrayDeserializer : JsonDeserializer<ByteArray> {
        override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): ByteArray = Hex.decode(json.asString)
    }

    class ByteArraySerializer : JsonSerializer<ByteArray> {
        override fun serialize(src: ByteArray, typeOfSrc: Type?, context: JsonSerializationContext?) = JsonPrimitive(src.toHex())
    }

    companion object {
        val sha256: MessageDigest = MessageDigest.getInstance("SHA-256")
        val gson: Gson = GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(Event::class.java, EventSerializer())
                .registerTypeAdapter(Event::class.java, EventDeserializer())
                .registerTypeAdapter(ByteArray::class.java, ByteArraySerializer())
                .registerTypeAdapter(ByteArray::class.java, ByteArrayDeserializer())
                .create()

    }

    fun generateId(): ByteArray {
        val rawEvent = listOf(
                0,
                pubKey,
                createdAt,
                kind,
                tags,
                content
        )
        val rawEventJson = gson.toJson(rawEvent)
        return sha256.digest(rawEventJson.toByteArray())
    }

}
