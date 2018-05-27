package com.jaychang.sac

import retrofit2.Converter
import java.lang.reflect.Type

interface JsonParser {
  fun getConverterFactory(): Converter.Factory

  fun <T> parse(json: String, typeOfT: Type, keyPath: String? = null): T

  /**
   * Update the parser to deserialize response by keyPath
   * */
  fun update(type: Type, keyPath: String) {
  }
}