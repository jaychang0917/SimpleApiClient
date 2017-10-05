package com.jaychang.sac.interceptor

import com.jaychang.sac.ApiManager
import okhttp3.Interceptor
import okhttp3.Response

class MockDataInterceptor(private val mockDataApis: ApiManager.MockDataApis) : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    val response = chain.proceed(request)
    val isMockDataEnabled = mockDataApis.hasApi(request)
    return if (isMockDataEnabled) {
      // fake okHttp that it is a successful response to run our mock data response converter
      response.newBuilder().code(200).build()
    } else {
      response
    }
  }

}