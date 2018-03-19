package com.jaychang.sac.converter

import com.google.gson.reflect.TypeToken
import com.jaychang.sac.SimpleApiResult
import com.jaychang.sac.annotation.WrappedResponse
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class WrappedResponseConverterFactory : Converter.Factory() {

  companion object {
    @JvmStatic
    fun create(): WrappedResponseConverterFactory {
      return WrappedResponseConverterFactory()
    }
  }

  override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
    // if the response type is not annotated @Unwrap, delegate to next converter
    if (annotations.none { it is WrappedResponse }) {
      return null
    }

    val wrappedType = annotations.find { it is WrappedResponse } as WrappedResponse

    val apiResultType = TypeToken.getParameterized(wrappedType.value.java, type).type

    val delegate: Converter<ResponseBody, Any> = retrofit.nextResponseBodyConverter(this, apiResultType, annotations)

    return Converter<ResponseBody, Any> { body ->
      (delegate.convert(body) as SimpleApiResult<Any>).result
    }
  }

}