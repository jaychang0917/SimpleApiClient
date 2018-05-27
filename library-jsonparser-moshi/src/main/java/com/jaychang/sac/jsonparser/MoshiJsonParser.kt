package com.jaychang.sac

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Converter
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.reflect.Type

class MoshiJsonParser: JsonParser {
  private var parser = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

  override fun getConverterFactory(): Converter.Factory = MoshiConverterFactory.create(parser)

  override fun <T> parse(json: String, typeOfT: Type, keyPath: String?): T {
    return if (keyPath == null) {
      parser.adapter<T>(typeOfT).fromJson(json)!!
    } else {
      createParser(keyPath).adapter<T>(typeOfT).fromJson(json)!!
    }
  }

  override fun update(type: Type, keyPath: String) {
    parser = createParser(keyPath)
  }

  private fun createParser(keyPath: String): Moshi {
     return Moshi.Builder().add(KeyPathJsonAdapterFactory(keyPath)).add(KotlinJsonAdapterFactory()).build()
  }

  private class KeyPathJsonAdapterFactory(val keyPath: String): JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>?, moshi: Moshi?): JsonAdapter<*>? {
      return KeyPathJsonAdapter(type, keyPath)
    }
  }

  private class KeyPathJsonAdapter(val type: Type, val keyPath: String): JsonAdapter<Any>() {
    override fun toJson(writer: JsonWriter, value: Any?) = Unit

    override fun fromJson(reader: JsonReader): Any {
      val map = reader.readJsonValue() as Map<*, *>
      val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
      var jsonObject = map
      val parts = keyPath.split(".")
      var jsonElement = jsonObject[parts[0]]
      for (part in parts) {
        jsonElement = jsonObject[part]
        if (jsonElement is Map<*,*>) {
          jsonObject = jsonElement
        }
      }
      return moshi.adapter<Any>(type).fromJsonValue(jsonElement)!!
    }
  }
}