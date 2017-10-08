package com.jaychang.sac

import retrofit2.Converter
import java.lang.reflect.Type

interface JsonParser {

  fun converterFactory(): Converter.Factory

  fun <T> parse(json: String, typeOfT: Type): T

}