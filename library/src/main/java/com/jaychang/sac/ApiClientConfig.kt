package com.jaychang.sac

import kotlin.reflect.KClass

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
  var certificatePins: List<CertificatePin>? = null
)