package com.jaychang.sac.converter

import com.jaychang.sac.util.Utils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.http.Part
import java.io.File
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class FileConverterFactory : Converter.Factory() {
  companion object {
    @JvmStatic
    fun create(): FileConverterFactory {
      return FileConverterFactory()
    }
  }

  override fun requestBodyConverter(type: Type, parameterAnnotations: Array<out Annotation>, methodAnnotations: Array<out Annotation>, retrofit: Retrofit): Converter<*, RequestBody>? {
    val isFileType = isFileType(type)
    val isListOfFileType = isListOfFileType(type)

    if (!isFileType && !isListOfFileType) {
      return null
    }

    val annotation = parameterAnnotations.find { annotation -> annotation is Part } as Part

    return Converter<Any, RequestBody> { value ->
      when {
        isFileType -> createMultipartBody(listOf(value as File), annotation)
        else -> createMultipartBody(value as Iterable<File>, annotation)
      }
    }
  }

  private fun isFileType(type: Type): Boolean {
    return type == File::class.java
  }

  private fun isListOfFileType(type: Type): Boolean {
    val rawType = getRawType(type)
    
    val isList = Iterable::class.java.isAssignableFrom(rawType)

    val isParameterizedType = type is ParameterizedType

    if (isList && isParameterizedType) {
      val innerType = getParameterUpperBound(0, type as ParameterizedType)
      return isFileType(innerType)
    }

    return false
  }

  private fun createMultipartBody(list: Iterable<File>, part: Part): RequestBody {
    val parts = list.map {
      val file = File(it.path)
      MultipartBody.Part.createFormData(part.value, file.name, RequestBody.create(MediaType.parse(Utils.getMimeType(file)), file))
    }

    val builder = MultipartBody.Builder()

    builder.setType(MultipartBody.FORM)

    parts.forEach { builder.addPart(it) }

    return builder.build()
  }
}