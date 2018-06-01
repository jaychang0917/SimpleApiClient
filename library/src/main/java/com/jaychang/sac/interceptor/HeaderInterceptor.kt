package com.jaychang.sac.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(private val headers: Map<String, String>) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val oldRequest = chain.request()
    val builder = oldRequest.newBuilder()
    headers.forEach { (key, value) ->
      builder.addHeader(key, value)
    }
    val newRequest = builder.build()
    return chain.proceed(newRequest)
  }
}