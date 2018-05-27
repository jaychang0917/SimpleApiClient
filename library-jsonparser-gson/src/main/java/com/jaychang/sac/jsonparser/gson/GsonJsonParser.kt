package com.jaychang.sac.jsonparser.gson

import com.google.gson.*
import retrofit2.Converter
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

class GsonJsonParser : com.jaychang.sac.JsonParser {

  private var gson = Gson()

  override fun getConverterFactory(): Converter.Factory {
    return GsonConverterFactory.create(gson)
  }

  override fun <T> parse(json: String, typeOfT: Type, keyPath: String?): T {
    return if (keyPath == null) {
      gson.fromJson(json, typeOfT)
    } else {
      createGson(typeOfT, keyPath).fromJson(json, typeOfT)
    }
  }

  override fun update(type: Type, keyPath: String) {
    gson = createGson(type, keyPath)
  }

  private fun createGson(type: Type, keyPath: String): Gson {
    val deserializer = KeyPathDeserializer(keyPath)
    return GsonBuilder().registerTypeAdapter(type, deserializer).create()
  }

  private class KeyPathDeserializer(private val keyPath: String) : JsonDeserializer<Any> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Any {
      val parts = keyPath.split(".")
      var jsonObject = json.asJsonObject
      var jsonElement: JsonElement = jsonObject[parts[0]]
      for (part in parts) {
        jsonElement = jsonObject[part]
        if (jsonElement is JsonObject) {
          jsonObject = jsonElement.asJsonObject
        }
      }
      return Gson().fromJson(jsonElement, typeOfT)
    }
  }

}
