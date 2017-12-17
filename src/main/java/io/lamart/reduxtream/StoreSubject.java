package io.lamart.reduxtream;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.Subject;

public class StoreSubject<T> extends Observable<T> implements Observer<Object> {

    private final Subject<Object> observer;
    private final Observable<T> observable;

    public StoreSubject(Subject<Object> subject, StoreTransformer<T> transformer) {
        this.observer = subject;
        this.observable = subject.compose(transformer);
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        observable.subscribe(observer);
    }

    @Override
    public void onSubscribe(Disposable d) {
        observer.onSubscribe(d);
    }

    @Override
    public void onNext(Object o) {
        observer.onNext(o);
    }

    @Override
    public void onError(Throwable e) {
        observer.onError(e);
    }

    @Override
    public void onComplete() {
        observer.onComplete();
    }
}
