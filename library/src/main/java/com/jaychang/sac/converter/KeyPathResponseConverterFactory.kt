package com.jaychang.sac.converter

import com.jaychang.sac.JsonParser
import com.jaychang.sac.annotations.ResponseKeyPath
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class KeyPathResponseConverterFactory(val jsonParser: JsonParser) : Converter.Factory() {

  companion object {
    @JvmStatic
    fun create(jsonParser: JsonParser): KeyPathResponseConverterFactory {
      return KeyPathResponseConverterFactory(jsonParser)
    }
  }

  override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
    // if the response type is not annotated @ResponseKeyPath, delegate to next converter
    if (annotations.none { it is ResponseKeyPath }) {
      return null
    }
    val keyPath = annotations.find { it is ResponseKeyPath } as ResponseKeyPath

    val value = keyPath.value
    if (value.isBlank() || value.split(".").isEmpty()) {
      return null
    }

    jsonParser.onKeyPathReceived(type, value)

    val delegate: Converter<ResponseBody, Any> = retrofit.nextResponseBodyConverter(this, type, annotations)

    return Converter<ResponseBody, Any> { body ->
      delegate.convert(body)
    }
  }

}