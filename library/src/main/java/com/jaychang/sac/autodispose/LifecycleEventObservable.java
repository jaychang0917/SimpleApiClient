package com.jaychang.sac.autodispose;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.Lifecycle.Event;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.os.Looper;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.subjects.BehaviorSubject;

class LifecycleEventsObservable extends Observable<Event> {

  private final Lifecycle lifecycle;
  private final BehaviorSubject<Event> eventsObservable = BehaviorSubject.create();

  @SuppressWarnings("CheckReturnValue") LifecycleEventsObservable(Lifecycle lifecycle) {
    this.lifecycle = lifecycle;
    Event event = Event.ON_ANY;
    switch (lifecycle.getCurrentState()) {
      case INITIALIZED:
        event = Event.ON_CREATE;
        break;
      case CREATED:
        event = Event.ON_START;
        break;
      case STARTED:
        event = Event.ON_RESUME;
        break;
      case RESUMED:
        event = Event.ON_RESUME;
        break;
      case DESTROYED:
        event = Event.ON_DESTROY;
        break;
    }
    eventsObservable.onNext(event);
  }

  Event getValue() {
    return eventsObservable.getValue();
  }

  @Override protected void subscribeActual(Observer<? super Event> observer) {
    if (Looper.myLooper() != Looper.getMainLooper()) {
      observer.onError(
        new IllegalStateException("Lifecycles can only be bound to on the main thread!"));
      return;
    }
    ArchLifecycleObserver archObserver =
      new ArchLifecycleObserver(lifecycle, observer, eventsObservable);
    observer.onSubscribe(archObserver);
    lifecycle.addObserver(archObserver);
  }

  static final class ArchLifecycleObserver extends MainThreadDisposable
    implements LifecycleObserver {
    private final Lifecycle lifecycle;
    private final Observer<? super Event> observer;
    private final BehaviorSubject<Event> eventsObservable;

    ArchLifecycleObserver(Lifecycle lifecycle, Observer<? super Event> observer,
                          BehaviorSubject<Event> eventsObservable) {
      this.lifecycle = lifecycle;
      this.observer = observer;
      this.eventsObservable = eventsObservable;
    }

    @Override protected void onDispose() {
      lifecycle.removeObserver(this);
    }

    @OnLifecycleEvent(Event.ON_ANY) void onStateChange(LifecycleOwner owner, Event event) {
      if (!isDisposed()) {
        eventsObservable.onNext(event);
        observer.onNext(event);
      }
    }
  }
}