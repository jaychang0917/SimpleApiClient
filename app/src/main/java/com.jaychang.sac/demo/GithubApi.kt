package com.jaychang.sac.demo

import com.jaychang.sac.*
import com.jaychang.sac.annotation.KeyPathResponse
import com.jaychang.sac.annotation.MockResponse
import com.jaychang.sac.jsonparser.moshi.MoshiJsonParser
import com.squareup.moshi.Json
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

class ApiResult<T : Any> : SimpleApiResult<T> {
  @Json(name = "items")
  override lateinit var result: T
}

class ApiError : SimpleApiError {
  @Json(name = "message")
  override lateinit var message: String
}

interface GithubApi {

  companion object {
    fun create(): GithubApi =
      SimpleApiClient.create {
        baseUrl = "https://api.github.com"
        defaultParameters = mapOf()
        defaultHeaders = mapOf()
        connectTimeout = TimeUnit.MINUTES.toMillis(1)
        readTimeout = TimeUnit.MINUTES.toMillis(1)
        writeTimeout = TimeUnit.MINUTES.toMillis(1)
        logLevel = LogLevel.BASIC // default: NONE
        certificatePins = listOf(
          CertificatePin(hostname = "api.foo.com", sha1PublicKeyHash = "0beec7b5ea3f0fdbc95d0dd47f3c5bc275da8a33"),
          CertificatePin(hostname = "api.bar.com", sha256PublicKeyHash = "fcde2b2edba56bf408601fb721fe9b5c338d10ee429ea04fae5511b68fbf8fb9")
        )

        interceptors = listOf()
        networkInterceptors = listOf()
        //httpClient = OkHttpClient.Builder().build()

        errorMessageKeyPath = "message"
        //errorClass = ApiError::class
        isMockResponseEnabled = true // default: false

        jsonParser = MoshiJsonParser()
        errorHandler = { error ->
          when (error) {
            is AuthenticationError -> {}
            is ClientError -> {}
            is ServerError -> {}
            is NetworkError -> {}
            is SSLError -> {}
          }
        }
      }
  }

  @GET("/search/users")
  @KeyPathResponse("foo.bar.items")
//  @WrappedResponse(ApiResult::class)
  @MockResponse(R.raw.get_users)
  fun getUsers(@Query("q") query: String): Single<List<User>>
}
