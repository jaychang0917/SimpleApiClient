package com.jaychang.sac

import android.arch.lifecycle.LifecycleOwner
import android.view.View
import com.jaychang.sac.autodispose.AutoDisposeLifecycleOwnerProxy
import com.jaychang.sac.autodispose.AutoDisposeLifecycleOwnerProxyImpl
import com.jaychang.sac.autodispose.AutoDisposeViewProxy
import com.jaychang.sac.autodispose.AutoDisposeViewProxyImpl
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.ObservableSubscribeProxy
import com.uber.autodispose.android.ViewScopeProvider
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit

fun <T, R> Observable<T>.then(mapper: (T) -> ObservableSource<R>): Observable<R> {
  return flatMap(mapper)
}

fun <T> Observable<T>.thenAll(vararg calls: ObservableSource<*>): Observable<Array<Any>> {
  return flatMap {
    Observable.zip(calls.asIterable(), { objects -> objects })
  }
}

fun <T> Observable<T>.retryExponential(maxRetryCount: Int = Int.MAX_VALUE, delaySeconds: Long = 0): Observable<T> {
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

fun <T> Observable<T>.retryInterval(maxRetryCount: Int = Int.MAX_VALUE, delaySeconds: Long = 0): Observable<T> {
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

fun <T> Observable<T>.autoDispose(lifecycleOwner: LifecycleOwner): AutoDisposeLifecycleOwnerProxy<T> {
  return AutoDisposeLifecycleOwnerProxyImpl(this, lifecycleOwner)
}

fun <T> Observable<T>.autoDispose(view: View): AutoDisposeViewProxy<T> {
  return AutoDisposeViewProxyImpl(this, view)
}

fun <T> AutoDisposeLifecycleOwnerProxy<T>.observe(onStart: () -> Unit = {}, onEnd: () -> Unit = {}, onSuccess: (T) -> Unit = {}, onError: (Throwable) -> Unit = {}): Disposable {
  return sourceObservable.doOnSubscribe { onStart() }.doFinally { onEnd() }
    .to<ObservableSubscribeProxy<T>>(AutoDispose.with(AndroidLifecycleScopeProvider.from(lifecycleOwner)).forObservable())
    .subscribe(Consumer { onSuccess(it) }, ErrorConsumer(onError))
}

fun <T> AutoDisposeViewProxy<T>.observe(onStart: () -> Unit = {}, onEnd: () -> Unit = {}, onSuccess: (T) -> Unit = {}, onError: (Throwable) -> Unit = {}): Disposable {
  return sourceObservable.doOnSubscribe { onStart() }.doFinally { onEnd() }
    .to<ObservableSubscribeProxy<T>>(AutoDispose.with(ViewScopeProvider.from(view)).forObservable())
    .subscribe(Consumer { onSuccess(it) }, ErrorConsumer(onError))
}

fun <T> Observable<T>.observe(onStart: () -> Unit = {}, onEnd: () -> Unit = {}, onSuccess: (T) -> Unit = {}, onError: (Throwable) -> Unit = {}): Disposable {
  return doOnSubscribe { onStart() }.doFinally { onEnd() }
    .subscribe(Consumer { onSuccess(it) }, ErrorConsumer(onError))
}