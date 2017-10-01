# SimpleApiClient
[![Release](https://jitpack.io/v/jaychang0917/SimpleApiClient.svg)](https://jitpack.io/#jaychang0917/SimpleApiClient)

A retrofit extension written in kotlin

## Table of Contents
* [Basic Usage](#basic_usage)
* [Unwrap Api Response](#unwrap)
* [Convert Uri to MultiPartBody](#image)
* [Serial / Parallel Calls](#serial_parallel_calls)
* [Retry Interval / Exponential backoff](#retry)
* [Auto Call Cancellation](#auto_cancel)

## Installation
In your project level build.gradle :

```java
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

In your app level build.gradle :

```java
dependencies {
    compile 'com.github.jaychang0917:SimpleApiClient:{latest_version}'
}
```
[![Release](https://jitpack.io/v/jaychang0917/SimpleApiClient.svg)](https://jitpack.io/#jaychang0917/SimpleApiClient)

---

## <a name=basic_usage>Basic Usage</a>
### Step 1
Configurate the api client and use it to create your api. `ApiError` is the error response model. You can centralize the handling of general error like 403 authentication in `errorHandler` block.
```java
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
          // handle error
        }
      }
  }

  @GET("/search/users")
  fun getUsers(@Query("q") query: String): Observable<List<User>>

}
````

### Step 2
Use `observe()` to enqueue the call, do your stuff in corresponding parameter block. All blocks are run at android main thread by default and they are optional.
```java
githubApi.getUsers("google")
  .observe(
    onStart = { println("show loading") },
    onEnd = { println("hide loading") },
    onSuccess = { println(it) },
    onError = { println(it.message) }
  )
```

## <a name=unwrap>Unwrap Api Response</a>
Sometimes the api response includes metadata that we don't need, but in order to map the response we create a wrapper class and return that wrapper class.
This approach leaks the implementation of service to calling code.

Assuming the response json looks like the following:
```xml
{
  total_count: 33909,
  incomplete_results: false,
  items: [
    {
      login: "jay",
      id: 965580,
      ...
    }
    ...
  ]
}
```
And you only want the `items` part, we can use `@Unwrap(ApiResult::class)` annotation to indicate that the return type is a unwrapped type of `ApiResult`,
which is `List<User>` in this case.
```java
class ApiResult<T: Any>: SimpleApiResult<T> {
  @SerializedName("items")
  override lateinit var result: T
}

@GET("/search/users")
@Unwrap(ApiResult::class)
fun getUsers(@Query("q") query: String): Observable<List<User>>
```

## <a name=image>Convert Uri to MultiPartBody</a>
Use `@Image("key")` to annotate a `Uri` or `ArrayList<Uri>` that is going to be converted to `MultiPartBody`
```java
@POST("/upload")
fun uploadPhoto(@Body @Image("image") file: Uri): Observable<ResponseBody>

@POST("/upload")
fun uploadPhotos(@Body @Image("image") files: ArrayList<Uri>): Observable<ResponseBody>
```

```java
githubApi.uploadPhoto(uri)
  .observe(...)
```

## <a name=serial_parallel_calls>Serial / Parallel Calls</a>
### Serial
```java
githubApi.getUsers("google")
  .then { githubApi.getRepo("google", "gson") }
  .observe(...)
```

### Serial then Parallel
```java
githubApi.getUsers("google")
  .then { githubApi.getRepo("google", "gson") }
  .thenAll(
    githubApi.getUsers("ReactiveX"),
    githubApi.getRepo("ReactiveX", "RxJava")
  )
  .observe(...)
```

### Parallel
```java
SimpleApiClient.all(
  githubApi.getUsers("google"),
  githubApi.getRepo("google", "gson")
).observe(...)
```

### Parallel then Serial
```java
SimpleApiClient.all(
  githubApi.getUsers("google"),
  githubApi.getRepo("google", "gson")
).then {
  githubApi.getUsers("google")
}.observe(...)
```

## <a name=retry>Retry Interval / Exponential backoff</a>
```java
githubApi.getUsers("google")
  .retryInterval(maxRetryCount = 3, delaySeconds = 5) // retry up to 3 times, each time delays 5 seconds
  .retryExponential(maxRetryCount = 3, delaySeconds = 5) // retry up to 3 times, each time delays 5^n seconds, where n = {1,2,3}
  .observe(...)
```

## <a name=auto_cancel>Auto Call Cancellation</a>
To avoid leaking context, we should cancel the executing api request when leave the context. Thanks to [AutoDispose](https://github.com/uber/AutoDispose), it is just a line of code to fix it. The api call will be cancelled automatically in corresponding lifecycle callback. For instance, an api call is made in `onStart()`, it be will cancelled automatically in `onStop`.

```java
githubApi.getUsers("google")
  .autoDispose(this)
  .observe(...)
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
