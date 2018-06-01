@file:JvmName("SimpleApiClientEx")

package com.jaychang.sac

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.jaychang.sac.util.Utils
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindToLifecycle
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.reactivestreams.Publisher
import java.io.File
import java.util.concurrent.TimeUnit

fun <T, R> Observable<T>.then(mapper: (T) -> ObservableSource<R>): Observable<R> {
  return flatMap(mapper)
}

fun <T, R> Flowable<T>.then(mapper: (T) -> Publisher<R>): Flowable<R> {
  return flatMap(mapper)
}

fun <T, R> Single<T>.then(mapper: (T) -> SingleSource<R>): Single<R> {
  return flatMap(mapper)
}

fun <T, R> Maybe<T>.then(mapper: (T) -> MaybeSource<R>): Maybe<R> {
  return flatMap(mapper)
}


fun <T> Observable<T>.thenAll(vararg calls: ObservableSource<*>): Observable<Array<Any>> {
  return flatMap {
    Observable.zip(calls.asIterable(), { objects -> objects })
  }
}

fun <T> Flowable<T>.thenAll(vararg calls: Publisher<*>): Flowable<Array<Any>> {
  return flatMap {
    Flowable.zip(calls.asIterable(), { objects -> objects })
  }
}

fun <T> Single<T>.thenAll(vararg calls: SingleSource<*>): Single<Array<Any>> {
  return flatMap {
    Single.zip(calls.asIterable(), { objects -> objects })
  }
}

fun <T> Maybe<T>.thenAll(vararg calls: MaybeSource<*>): Maybe<Array<Any>> {
  return flatMap {
    Maybe.zip(calls.asIterable(), { objects -> objects })
  }
}


fun <T> Observable<T>.retryExponential(maxRetryCount: Int = Int.MAX_VALUE, delaySeconds: Long): Observable<T> {
  return retryWhen { error ->
    error
      .zipWith(Observable.range(1, maxRetryCount + 1), BiFunction { throwable: Throwable, retryCount: Int -> (throwable to retryCount) })
      .flatMap { pair ->
        if (pair.second == maxRetryCount + 1) {
          Observable.error(pair.first)
        } else {
          Observable.timer(Math.pow(delaySeconds.toDouble(), pair.second.toDouble()).toLong(), TimeUnit.SECONDS)
        }
      }
  }
}

fun <T> Flowable<T>.retryExponential(maxRetryCount: Int = Int.MAX_VALUE, delaySeconds: Long): Flowable<T> {
  return retryWhen { error ->
    error
      .zipWith(Flowable.range(1, maxRetryCount + 1), BiFunction { throwable: Throwable, retryCount: Int -> (throwable to retryCount) })
      .flatMap { pair ->
        if (pair.second == maxRetryCount + 1) {
          Flowable.error(pair.first)
        } else {
          Flowable.timer(Math.pow(delaySeconds.toDouble(), pair.second.toDouble()).toLong(), TimeUnit.SECONDS)
        }
      }
  }
}

fun <T> Single<T>.retryExponential(maxRetryCount: Int = Int.MAX_VALUE, delaySeconds: Long): Single<T> {
  return retryWhen { error ->
    error
      .zipWith(Flowable.range(1, maxRetryCount + 1), BiFunction { throwable: Throwable, retryCount: Int -> (throwable to retryCount) })
      .flatMap { pair ->
        if (pair.second == maxRetryCount + 1) {
          Flowable.error(pair.first)
        } else {
          Flowable.timer(Math.pow(delaySeconds.toDouble(), pair.second.toDouble()).toLong(), TimeUnit.SECONDS)
        }
      }
  }
}

fun <T> Maybe<T>.retryExponential(maxRetryCount: Int = Int.MAX_VALUE, delaySeconds: Long): Maybe<T> {
  return retryWhen { error ->
    error
      .zipWith(Flowable.range(1, maxRetryCount + 1), BiFunction { throwable: Throwable, retryCount: Int -> (throwable to retryCount) })
      .flatMap { pair ->
        if (pair.second == maxRetryCount + 1) {
          Flowable.error(pair.first)
        } else {
          Flowable.timer(Math.pow(delaySeconds.toDouble(), pair.second.toDouble()).toLong(), TimeUnit.SECONDS)
        }
      }
  }
}


fun <T> Observable<T>.retryInterval(maxRetryCount: Int = Int.MAX_VALUE, delaySeconds: Long): Observable<T> {
  return retryWhen { error ->
    error
      .zipWith(Observable.range(1, maxRetryCount + 1), BiFunction { throwable: Throwable, retryCount: Int -> (throwable to retryCount) })
      .flatMap { pair ->
        if (pair.second == maxRetryCount + 1) {
          Observable.error(pair.first)
        } else {
          Observable.timer(delaySeconds, TimeUnit.SECONDS)
        }
      }
  }
}

fun <T> Flowable<T>.retryInterval(maxRetryCount: Int = Int.MAX_VALUE, delaySeconds: Long): Flowable<T> {
  return retryWhen { error ->
    error
      .zipWith(Flowable.range(1, maxRetryCount + 1), BiFunction { throwable: Throwable, retryCount: Int -> (throwable to retryCount) })
      .flatMap { pair ->
        if (pair.second == maxRetryCount + 1) {
          Flowable.error(pair.first)
        } else {
          Flowable.timer(delaySeconds, TimeUnit.SECONDS)
        }
      }
  }
}

fun <T> Single<T>.retryInterval(maxRetryCount: Int = Int.MAX_VALUE, delaySeconds: Long): Single<T> {
  return retryWhen { error ->
    error
      .zipWith(Flowable.range(1, maxRetryCount + 1), BiFunction { throwable: Throwable, retryCount: Int -> (throwable to retryCount) })
      .flatMap { pair ->
        if (pair.second == maxRetryCount + 1) {
          Flowable.error(pair.first)
        } else {
          Flowable.timer(delaySeconds, TimeUnit.SECONDS)
        }
      }
  }
}

fun <T> Maybe<T>.retryInterval(maxRetryCount: Int = Int.MAX_VALUE, delaySeconds: Long): Maybe<T> {
  return retryWhen { error ->
    error
      .zipWith(Flowable.range(1, maxRetryCount + 1), BiFunction { throwable: Throwable, retryCount: Int -> (throwable to retryCount) })
      .flatMap { pair ->
        if (pair.second == maxRetryCount + 1) {
          Flowable.error(pair.first)
        } else {
          Flowable.timer(delaySeconds, TimeUnit.SECONDS)
        }
      }
  }
}


fun <T> Observable<T>.autoCancel(lifecycleOwner: LifecycleOwner, untilEvent: Lifecycle.Event? = null): Observable<T> {
  return if (untilEvent != null) {
    bindUntilEvent(lifecycleOwner, untilEvent)
  } else {
    bindToLifecycle(lifecycleOwner)
  }
}

fun <T> Flowable<T>.autoCancel(lifecycleOwner: LifecycleOwner, untilEvent: Lifecycle.Event? = null): Flowable<T> {
  return if (untilEvent != null) {
    bindUntilEvent(lifecycleOwner, untilEvent)
  } else {
    bindToLifecycle(lifecycleOwner)
  }
}

fun <T> Single<T>.autoDispose(lifecycleOwner: LifecycleOwner, untilEvent: Lifecycle.Event? = null): Single<T> {
  return if (untilEvent != null) {
    bindUntilEvent(lifecycleOwner, untilEvent)
  } else {
    bindToLifecycle(lifecycleOwner)
  }
}

fun <T> Maybe<T>.autoDispose(lifecycleOwner: LifecycleOwner, untilEvent: Lifecycle.Event? = null): Maybe<T> {
  return if (untilEvent != null) {
    bindUntilEvent(lifecycleOwner, untilEvent)
  } else {
    bindToLifecycle(lifecycleOwner)
  }
}

fun Completable.autoDispose(lifecycleOwner: LifecycleOwner, untilEvent: Lifecycle.Event? = null): Completable {
  return if (untilEvent != null) {
    bindUntilEvent(lifecycleOwner, untilEvent)
  } else {
    bindToLifecycle(lifecycleOwner)
  }
}


fun <T> Observable<T>.observe(onStart: () -> Unit = {}, onEnd: () -> Unit = {}, onSuccess: (T) -> Unit = {}, onError: (Throwable) -> Unit = {}): Disposable {
  return doOnSubscribe { onStart() }.doFinally { onEnd() }
    .subscribe(Consumer { onSuccess(it) }, ErrorConsumer(onError))
}

fun <T> Flowable<T>.observe(onStart: () -> Unit = {}, onEnd: () -> Unit = {}, onSuccess: (T) -> Unit = {}, onError: (Throwable) -> Unit = {}): Disposable {
  return doOnSubscribe { onStart() }.doFinally { onEnd() }
    .subscribe(Consumer { onSuccess(it) }, ErrorConsumer(onError))
}

fun <T> Single<T>.observe(onStart: () -> Unit = {}, onEnd: () -> Unit = {}, onSuccess: (T) -> Unit = {}, onError: (Throwable) -> Unit = {}): Disposable {
  return doOnSubscribe { onStart() }.doFinally { onEnd() }
    .subscribe(Consumer { onSuccess(it) }, ErrorConsumer(onError))
}

fun <T> Maybe<T>.observe(onStart: () -> Unit = {}, onEnd: () -> Unit = {}, onSuccess: (T) -> Unit = {}, onError: (Throwable) -> Unit = {}): Disposable {
  return doOnSubscribe { onStart() }.doFinally { onEnd() }
    .subscribe(Consumer { onSuccess(it) }, ErrorConsumer(onError))
}

fun Completable.observe(onStart: () -> Unit = {}, onEnd: () -> Unit = {}, onSuccess: () -> Unit = {}, onError: (Throwable) -> Unit = {}): Disposable {
  return doOnSubscribe { onStart() }.doFinally { onEnd() }
    .subscribe(Action { onSuccess() }, ErrorConsumer(onError))
}

fun File.toMultipartBodyPart(paramName: String, mimeType: String = Utils.getMimeType(this)) {
  MultipartBody.Part.createFormData(paramName, name, RequestBody.create(MediaType.parse(mimeType), this))
}