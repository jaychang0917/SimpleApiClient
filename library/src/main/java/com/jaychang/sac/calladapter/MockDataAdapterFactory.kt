package com.jaychang.sac.calladapter

import com.jaychang.sac.ApiManager
import com.jaychang.sac.annotations.MockData
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.Type

internal class MockDataAdapterFactory(val mockDataApis: ApiManager.MockDataApis,  private val isEnabled: Boolean) : CallAdapter.Factory() {

  companion object {
    @JvmStatic
    fun create(mockDataApis: ApiManager.MockDataApis, isEnabled: Boolean): MockDataAdapterFactory {
      return MockDataAdapterFactory(mockDataApis, isEnabled)
    }
  }

  override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
    if (annotations.none { it is MockData } || !isEnabled) {
      return null
    }

    val delegate = retrofit.nextCallAdapter(this, returnType,
      annotations) as CallAdapter<Any, Observable<*>>

    return object : CallAdapter<Any, Any> {
      override fun adapt(call: Call<Any>): Any {
        mockDataApis.addApi(call.request())
        return delegate.adapt(call)
      }

      override fun responseType(): Type {
        return delegate.responseType()
      }
    }
  }
}