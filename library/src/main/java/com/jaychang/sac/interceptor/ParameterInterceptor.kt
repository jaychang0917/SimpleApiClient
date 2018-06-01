package com.jaychang.sac.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class ParameterInterceptor(private val params: Map<String,String>) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val oldRequest = chain.request()
    val builder = oldRequest.url().newBuilder()
    params.forEach { (key, value) ->
      builder.addQueryParameter(key, value)
    }
    val newRequest = oldRequest.newBuilder().url(builder.build()).build()
    return chain.proceed(newRequest)
  }
}