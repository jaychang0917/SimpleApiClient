package com.jaychang.sac

import com.google.gson.*
import retrofit2.Converter
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

class GsonParser: JsonParser {

  private var gson = Gson()

  override fun converterFactory(): Converter.Factory {
    return GsonConverterFactory.create(gson)
  }

  override fun <T> parse(json: String, typeOfT: Type): T {
    return gson.fromJson(json, typeOfT)
  }

  override fun onKeyPathReceived(type: Type, keyPath: String) {
    val deserializer = KeyPathDeserializer<Any>(keyPath)
    gson = GsonBuilder().registerTypeAdapter(type, deserializer).create()
  }

  class KeyPathDeserializer<T : Any>(private val keyPath: String) : JsonDeserializer<T> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): T {
      val parts = keyPath.split(".")
      var jsonObject = json.asJsonObject
      var jsonElement: JsonElement = jsonObject[parts[0]]
      for (part in parts) {
        jsonElement =  jsonObject[part]
        if (jsonElement is JsonObject) {
          jsonObject = jsonElement.asJsonObject
        }
      }
      return Gson().fromJson(jsonElement, typeOfT)
    }
  }

}