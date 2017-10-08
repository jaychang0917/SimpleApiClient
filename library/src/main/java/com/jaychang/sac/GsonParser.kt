package com.jaychang.sac

import com.google.gson.Gson
import retrofit2.Converter
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

class GsonParser: JsonParser {

  private val gson = Gson()

  override fun converterFactory(): Converter.Factory {
    return GsonConverterFactory.create()
  }

  override fun <T> parse(json: String, typeOfT: Type): T {
    return gson.fromJson(json, typeOfT)
  }

}