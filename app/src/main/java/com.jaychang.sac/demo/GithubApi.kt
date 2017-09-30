package com.jaychang.sac.demo

import android.net.Uri
import com.google.gson.annotations.SerializedName
import com.jaychang.sac.AuthenticationError
import com.jaychang.sac.SimpleApiClient
import com.jaychang.sac.SimpleApiError
import com.jaychang.sac.SimpleApiResult
import com.jaychang.sac.annotations.Image
import com.jaychang.sac.annotations.Unwrap
import com.jaychang.sac.demo.model.Repo
import com.jaychang.sac.demo.model.User
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*
import java.util.concurrent.TimeUnit

class ApiResult<T: Any>: SimpleApiResult<T> {
  @SerializedName("items")
  override lateinit var result: T
}

class ApiError : SimpleApiError {
  @SerializedName("message")
  override lateinit var message: String
}

interface GithubApi {

  companion object {
    fun create() : GithubApi =
      SimpleApiClient.create<GithubApi, ApiError> {
        baseUrl = "https://api.github.com"
        defaultParameters = mapOf()
        defaultHeaders = mapOf()
        connectTimeout = TimeUnit.MINUTES.toMillis(1)
        readTimeout = TimeUnit.MINUTES.toMillis(1)
        writeTimeout = TimeUnit.MINUTES.toMillis(1)
        errorHandler = { error ->
          if (error is AuthenticationError) {
            println("handle auth error")
          }
        }
      }
  }

  @GET("/search/users")
  @Unwrap(ApiResult::class)
  fun getUsers(@Query("q") query: String): Observable<List<User>>

  @GET("/repos/{user}/{repo}")
  fun getRepo(@Path("user") user: String, @Path("repo") repo: String): Observable<Repo>

  @POST("/upload")
  fun uploadPhoto(@Body @Image("image") file: Uri): Observable<ResponseBody>

  // retrofit doesn't support wildcard parameter like List<out T>,
  // you can solve it by using @JvmSuppressWildcards,
  // or you can use ArrayList<T> which is not wildcard
  @POST("/upload")
  fun uploadPhotos(@Body @Image("image") files: ArrayList<Uri>): Observable<ResponseBody>

}
