package com.jaychang.sac.converter

import android.net.Uri
import com.jaychang.sac.annotations.Image
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

class ImageConverterFactory : Converter.Factory() {

  companion object {
    @JvmStatic
    fun create(): ImageConverterFactory {
      return ImageConverterFactory()
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
      throw MissingArgumentException("""You must specify @Body""")
    }

    val hasNoImageAnnotation = parameterAnnotations.none { annotation -> annotation is Image }
    if (hasNoImageAnnotation) {
      throw MissingArgumentException("""You must specify @Image, e.g. @Image("foo")""")
    }

    val annotation = parameterAnnotations.find { annotation -> annotation is Image } as Image

    val annotationValue = annotation.value

    return Converter<Any, RequestBody> { value ->
      when {
        isUriType -> createMultipartBody(listOf(value as Uri), annotationValue)
        else -> createMultipartBody(value as Iterable<Uri>, annotationValue)
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

  private fun createMultipartBody(list: Iterable<Uri>, annotationValue: String): MultipartBody {
    val parts = list.map {
      val file = File(it.path)
      MultipartBody.Part.createFormData(annotationValue, file.name, RequestBody.create(MediaType.parse("image/*"), file))
    }

    val builder = MultipartBody.Builder()

    builder.setType(MultipartBody.FORM)

    parts.forEach { builder.addPart(it) }

    return builder.build()
  }

}