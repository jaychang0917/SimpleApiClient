package com.jaychang.sac

import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

typealias LogLevel = HttpLoggingInterceptor.Level

data class ApiClientConfig(
  var baseUrl: String = "",
  var connectTimeout: Long = TimeUnit.MINUTES.toMillis(1),
  var readTimeout: Long = TimeUnit.MINUTES.toMillis(1),
  var writeTimeout: Long = TimeUnit.MINUTES.toMillis(1),
  var defaultHeaders: Map<String, String>? = null,
  var defaultParameters: Map<String, String>? = null,
  var errorClass: KClass<*>? = null,
  var apiClass: KClass<*>? = null,
  var errorHandler: ((Throwable) -> Unit)? = null,
  var certificatePins: List<CertificatePin>? = null,
  var isStethoEnabled: Boolean = true,
  var logLevel: LogLevel = LogLevel.NONE,
  var isMockDataEnabled: Boolean = false
)