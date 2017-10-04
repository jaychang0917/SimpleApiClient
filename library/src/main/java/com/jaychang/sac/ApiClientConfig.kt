package com.jaychang.sac

import okhttp3.logging.HttpLoggingInterceptor
import kotlin.reflect.KClass

typealias LogLevel = HttpLoggingInterceptor.Level

data class ApiClientConfig(
  var baseUrl: String = "",
  var connectTimeout: Long = 10 * 6000,
  var readTimeout: Long = 10 * 6000,
  var writeTimeout: Long = 10 * 6000,
  var defaultHeaders: Map<String, String>? = null,
  var defaultParameters: Map<String, String>? = null,
  var errorClass: KClass<*>? = null,
  var apiClass: KClass<*>? = null,
  var errorHandler: ((Throwable) -> Unit)? = null,
  var certificatePins: List<CertificatePin>? = null,
  var enableStetho: Boolean = true,
  var logLevel: LogLevel = LogLevel.NONE
)