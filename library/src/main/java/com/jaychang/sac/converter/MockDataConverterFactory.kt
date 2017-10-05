package com.jaychang.sac.converter

import android.content.Context
import com.jaychang.sac.Utils
import com.jaychang.sac.annotations.MockData
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class MockDataConverterFactory(private val context: Context) : Converter.Factory() {

  companion object {
    @JvmStatic
    fun create(context: Context): MockDataConverterFactory {
      return MockDataConverterFactory(context)
    }
  }

  override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit): Converter<ResponseBody, Any>? {
    // if the response type is not annotated @MockData, delegate to next converter
    if (annotations.none { it is MockData }) {
      return null
    }

    val annotation = annotations.find { it is MockData } as MockData

    val delegate: Converter<ResponseBody, Any> = retrofit.nextResponseBodyConverter(this, type, annotations)

    return Converter { body ->
      val json = Utils.text(context, annotation.file)
      val mockDataResponseBody = ResponseBody.create(body.contentType(), json)
      // delegate to next converter
      delegate.convert(mockDataResponseBody)
    }
  }

}