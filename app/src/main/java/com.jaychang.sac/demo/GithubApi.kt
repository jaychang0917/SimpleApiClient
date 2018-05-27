package com.jaychang.sac.demo

import com.google.gson.annotations.SerializedName
import com.jaychang.sac.*
import com.jaychang.sac.annotation.KeyPathResponse
import com.jaychang.sac.annotation.MockResponse
import com.jaychang.sac.demo.model.Repo
import com.jaychang.sac.demo.model.User
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

class ApiResult<T : Any> : SimpleApiResult<T> {
  @SerializedName("items")
  override lateinit var result: T
}

class ApiError : SimpleApiError {
  @SerializedName("message")
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
        //httpClient = OkHttpClient.Builder().build() // your own http client, above configs will be overwritten

        errorMessageKeyPath = "message"
        //errorClass = ApiError::class
        isMockResponseEnabled = true // default: false

        jsonParser = GsonJsonParser() // default: GsonParser
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
  @MockResponse(R.raw.get_users)
  fun getUsers(@Query("q") query: String): Observable<List<User>>

  @GET("/repos/{user}/{repo}")
  fun getRepo(@Path("user") user: String, @Path("repo") repo: String): Observable<Repo>
}
