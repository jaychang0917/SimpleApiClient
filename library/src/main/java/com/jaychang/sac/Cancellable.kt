package com.jaychang.sac

import io.reactivex.disposables.Disposable

class Cancellable(private val disposable: Disposable) {

  fun cancel() = disposable.dispose()

  fun isCancelled() = disposable.isDisposed()

}