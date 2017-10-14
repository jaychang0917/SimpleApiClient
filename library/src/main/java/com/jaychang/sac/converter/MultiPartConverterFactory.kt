package com.jaychang.sac.converter

import android.net.Uri
import com.jaychang.sac.annotation.MultiPart
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.apache.commons.cli.MissingArgumentException
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.http.Body
import java.io.File
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class MultiPartConverterFactory : Converter.Factory() {

  companion object {
    @JvmStatic
    fun create(): MultiPartConverterFactory {
      return MultiPartConverterFactory()
    }
  }

  override fun requestBodyConverter(type: Type, parameterAnnotations: Array<out Annotation>, methodAnnotations: Array<out Annotation>, retrofit: Retrofit): Converter<*, RequestBody>? {
    val isUriType = isUriType(type)
    val isListOfUriType = isListOfUriType(type)

    if (!isUriType && !isListOfUriType) {
      return null
    }

    val hasNoBodyAnnotation = parameterAnnotations.none { annotation -> annotation is Body }
    if (hasNoBodyAnnotation) {
      throw MissingArgumentException("You must specify @Body")
    }

    val hasNoImageAnnotation = parameterAnnotations.none { annotation -> annotation is MultiPart }
    if (hasNoImageAnnotation) {
      throw MissingArgumentException("You must specify @MultiPart")
    }

    val annotation = parameterAnnotations.find { annotation -> annotation is MultiPart } as MultiPart

    return Converter<Any, RequestBody> { value ->
      when {
        isUriType -> createMultipartBody(listOf(value as Uri), annotation)
        else -> createMultipartBody(value as Iterable<Uri>, annotation)
      }
    }
  }

  private fun isUriType(type: Type): Boolean {
    return type == Uri::class.java
  }

  private fun isListOfUriType(type: Type): Boolean {
    val rawType = getRawType(type)
    
    val isList = Iterable::class.java.isAssignableFrom(rawType)

    val isParameterizedType = type is ParameterizedType

    if (isList && isParameterizedType) {
      val innerType = getParameterUpperBound(0, type as ParameterizedType)
      return isUriType(innerType)
    }

    return false
  }

  private fun createMultipartBody(list: Iterable<Uri>, part: MultiPart): MultipartBody {
    val parts = list.map {
      val file = File(it.path)
      MultipartBody.Part.createFormData(part.name, file.name, RequestBody.create(MediaType.parse(part.mimeType), file))
    }

    val builder = MultipartBody.Builder()

    builder.setType(MultipartBody.FORM)

    parts.forEach { builder.addPart(it) }

    return builder.build()
  }

}