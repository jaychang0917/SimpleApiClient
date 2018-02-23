# SimpleApiClient
[![Download](https://api.bintray.com/packages/jaychang0917/maven/simpleapiclient/images/download.svg) ](https://bintray.com/jaychang0917/maven/simpleapiclient/_latestVersion)
[![Android Weekly](http://img.shields.io/badge/Android%20Weekly-%23277-2CB3E5.svg)](http://androidweekly.net/issues/issue-277)

A configurable api client based on Retrofit2 and RxJava2 for android

## Table of Contents
* [Basic Usage](#basic_usage)
* [Unwrap Response by KeyPath](#unwrap_keypath)
* [Unwrap Response by Wrapper Class](#unwrap_class)
* [Convert Uri to MultiPartBody](#image)
* [Serial / Parallel Calls](#serial_parallel_calls)
* [Retry Interval / Exponential backoff](#retry)
* [Call Cancellation](#call_cancel)
* [Mock Response](#mock_response)

## Installation
In your app level build.gradle :

```java
dependencies {
    compile 'com.jaychang:simpleapiclient:2.0.0'
}
```
[![Download](https://api.bintray.com/packages/jaychang0917/maven/simpleapiclient/images/download.svg) ](https://bintray.com/jaychang0917/maven/simpleapiclient/_latestVersion)

---

## <a name=basic_usage>Basic Usage</a>
### Step 1
Config the api client and use it to create your api.
```java
interface GithubApi {

  companion object {
    fun create() : GithubApi =
      SimpleApiClient.create {
        baseUrl = "https://api.github.com" 
        errorClass = ApiError::class // should be conformed to SimpleApiError
        errorMessageKeyPath = "meta.message"
        defaultParameters = mapOf()
        defaultHeaders = mapOf()
        connectTimeout = TimeUnit.MINUTES.toMillis(1)
        readTimeout = TimeUnit.MINUTES.toMillis(1)
        writeTimeout = TimeUnit.MINUTES.toMillis(1)
        enableStetho = true // default true
        logLevel = LogLevel.BASIC // default NONE
        isMockResponseEnabled = true // default false
        certificatePins = listOf(
          CertificatePin(hostname = "api.foo.com", sha1PublicKeyHash = "0beec7b5ea3f0fdbc95d0dd47f3c5bc275da8a33"),
          CertificatePin(hostname = "api.bar.com", sha256PublicKeyHash = "fcde2b2edba56bf408601fb721fe9b5c338d10ee429ea04fae5511b68fbf8fb9")
        )
        jsonParser = GsonParser() // default: GsonParser
        errorHandler = { error ->
          // you can centralize the handling of general error here
          when (error) {
            is AuthenticationError -> {...}
            is ClientError -> {...}
            is ServerError -> {...}
            is NetworkError -> {...}
            is SSLError -> {...}
          }
        }
      }
  }

  @GET("/search/users")
  fun getUsers(@Query("q") query: String): Observable<List<User>>

}
````

#### Custom JSON Parser
The library uses Gson to parse json by default, you can create your own json parser by implementing `JsonParser` interface.
```kotlin
class MoshiParser : JsonParser {
  var moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

  override fun converterFactory(): Converter.Factory = MoshiConverterFactory.create(moshi)

  override fun <T> parse(json: String, typeOfT: Type): T {
    val jsonAdapter = moshi.adapter<T>(typeOfT)
    return jsonAdapter.fromJson(json)!!
  }
}
```

### Step 2
Use `observe()` to enqueue the call, do your stuff in corresponding parameter block. All blocks are run on android main thread by default and they are optional.
```kotlin
githubApi.getUsers("google")
  .observe(
    onStart = { println("show loading") },
    onEnd = { println("hide loading") },
    onSuccess = { println(it) },
    onError = { println(it.message) }
  )
```

## <a name=unwrap_keypath>Unwrap Response by KeyPath</a>
Sometimes the api response includes metadata that we don't need, but in order to map the response we create a wrapper class and make the function return that wrapper class.
This approach leaks the implementation of service to calling code.

Assuming the response json looks like the following:
```xml
{
  total_count: 33909,
  incomplete_results: false,
  foo: {
    bar: {
      items: [
        {
          login: "jaychang0917",
          ...
        }
        ...
      ]
    }
  }
}
```
And you only want the `items` part, use `@ResponseKeyPath("keypath")` annotation to indicate which part of response you want. 
```kotlin
@GET("/search/users")
@ResponseKeyPath("foo.bar.items")
fun getUsers(@Query("q") query: String): Observable<List<User>>
```

Similarly, unwrap the error response by setting the `errorMessageKeyPath` of `SimpleApiClient.Config`

**This feature is only available for default gson parser, if you use other json parser like *moshi*, you should implement the following method of `JsonParser`**
```kotlin
interface JsonParser {

  // this method is called before the api response parsing
  fun update(type: Type, keyPath: String) {
    
  }

}
```
[The default `GsonParser` implementation](https://github.com/jaychang0917/SimpleApiClient/blob/master/library/src/main/java/com/jaychang/sac/GsonParser.kt)

## <a name=unwrap_class>Unwrap Response by Wrapper Class</a>
An alternative solution is that you can create a wrapper class that conforming `SimpleApiResult<T>`, and use `@Unwrap(class)` to indicate that you want an unwrapped response of that wrapper class. 

```kotlin
class ApiResult<T: Any>: SimpleApiResult<T> {
  ...
}

@GET("/search/users")
@Unwrap(ApiResult::class)
fun getUsers(@Query("q") query: String): Observable<List<User>>
```

## <a name=image>Convert Uri to MultiPartBody</a>
Use `@MultiPart` to annotate a `Uri` or `ArrayList<Uri>` that is going to be converted to `MultiPartBody`
```kotlin
@POST("/upload")
fun uploadPhoto(@Body @MultiPart(name = "image", mimeType = "image/jpeg") file: Uri): Observable<Image>

@POST("/upload")
fun uploadPhotos(@Body @MultiPart(name = "image", mimeType = "image/jpeg") files: ArrayList<Uri>): Observable<Image>
```

```kotlin
githubApi.uploadPhoto(uri)
  .observe(...)
```

## <a name=serial_parallel_calls>Serial / Parallel Calls</a>
### Serial
```kotlin
githubApi.foo()
  .then { foo -> githubApi.bar(foo.name) }
  .observe(...)
```

### Serial then Parallel
```kotlin
githubApi.foo()
  .then { foo -> githubApi.bar(foo.name) }
  .thenAll( bar ->
    githubApi.baz(bar.name),
    githubApi.qux(bar.name)
  )
  .observe(...)
```

### Parallel
```kotlin
SimpleApiClient.all(
  githubApi.foo(),
  githubApi.bar()
).observe(...)
```

### Parallel then Serial
```kotlin
SimpleApiClient.all(
  githubApi.foo(),
  githubApi.bar()
).then { array -> // the return type is Array<Any>, you should cast them, e.g. val users = array[0] as List<User>
  githubApi.baz()
}.observe(...)
```

## <a name=retry>Retry Interval / Exponential backoff</a>
```kotlin
githubApi.getUsers("google")
  .retryInterval(maxRetryCount = 3, delaySeconds = 5) // retry up to 3 times, each time delays 5 seconds
  .retryExponential(maxRetryCount = 3, delaySeconds = 5) // retry up to 3 times, each time delays 5^n seconds, where n = {1,2,3}
  .observe(...)
```

## <a name=call_cancel>Call Cancellation</a>
### Auto Call Cancellation
To avoid leaking context, we should cancel the executing api request when leave the context. Thanks to [AutoDispose](https://github.com/uber/AutoDispose), it is just a line of code to fix it. The api call will be cancelled automatically in corresponding lifecycle callback. For instance, an api call is made in `onStart()`, it be will cancelled automatically in `onStop`.

```kotlin
githubApi.getUsers("google")
  .autoCancel(this)
  .observe(...)
```
### Cancel call manually
```kotlin
val call = githubApi.getUsers("google").observe(...)

call.cancel()
```

## <a name=mock_response>Mock Response</a>
To enable response mocking, set `SimpleApiClient.Config.isMockResponseEnabled` to `true`.
 
### Mock sample json data
To make the api return a successful response with provided json
```kotlin
@GET("/repos/{user}/{repo}")
@MockResponse(R.raw.get_repo)
fun getRepo(@Path("user") user: String, @Path("repo") repo: String): Observable<Repo>
```

### Mock status
To make the api return a client side error with provided json 
```kotlin
@GET("/repos/{user}/{repo}")
@MockResponse(json = R.raw.get_repo_error, status = Status.CLIENT_ERROR)
fun getRepo(@Path("user") user: String, @Path("repo") repo: String): Observable<Repo>
```
`json` parameter of `MockResponse` is optional, you can set the status only, then you receive empty string.

Possible `Status` values:
```kotlin
enum class Status {
  SUCCESS, AUTHENTICATION_ERROR, CLIENT_ERROR, SERVER_ERROR, NETWORK_ERROR, SSL_ERROR
}
```
To mock a response with success status only, you should return `Observable<Unit>`.
```kotlin
@DELETE("/repo/{id}}")
@MockResponse(status = Status.SUCCESS)
fun deleteRepo(@Path("id") id: String): Observable<Unit>
```

## License
```
Copyright 2017 Jay Chang

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
