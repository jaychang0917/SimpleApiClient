package com.jaychang.sac.calladapter

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jaychang.sac.SimpleApiResult
import com.jaychang.sac.Utils
import com.jaychang.sac.annotations.MockData
import com.jaychang.sac.annotations.Unwrap
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class MockDataAdapterFactory(private val isEnabled: Boolean, private val context: Context, private val gson: Gson) : CallAdapter.Factory() {

  companion object {
    @JvmStatic
    fun create(isEnabled: Boolean, context: Context, gson: Gson): MockDataAdapterFactory {
      return MockDataAdapterFactory(isEnabled, context, gson)
    }
  }

  override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
    if (annotations.none { it is MockData } || !isEnabled) {
      return null
    }

    val dataType = (returnType as ParameterizedType).actualTypeArguments[0]

    val apiResultType = if (annotations.any { it is Unwrap }) {
      val wrappedType = annotations.find { it is Unwrap } as Unwrap
      TypeToken.getParameterized(wrappedType.value.java, dataType).type
    } else {
      dataType
    }

    val annotation = annotations.find { it is MockData } as MockData

    return object : CallAdapter<Any, Any> {
      override fun adapt(call: Call<Any>): Any {
        return Observable.fromCallable {
          val json = Utils.text(context, annotation.file)
          val data: SimpleApiResult<Any> = gson.fromJson(json, apiResultType)
          data.result
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
      }

      override fun responseType(): Type {
        return apiResultType
      }
    }
  }
}