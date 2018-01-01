package io.lamart.xtream.store.implementation;

import io.lamart.xtream.state.State;
import io.lamart.xtream.store.Store;
import io.lamart.xtream.store.StoreInitializer;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;

import java.util.concurrent.Callable;

abstract class Instance<T> extends Store<T> {

    protected final Callable<T> getState;
    protected final Consumer<Object> dispatch;
    protected final Observable<T> observable;

    protected Instance(Callable<T> getState, Consumer<Object> dispatch, Observable<T> observable) {
        this.getState = getState;
        this.dispatch = dispatch;
        this.observable = observable;
    }

    protected static <T> Observable<T> apply(StoreInitializer<T> initializer, State<T> state, Consumer<Object> dispatch, Observable<Object> source) {
        try {
            final ConnectableObservable<T> observable = initializer.apply(state, dispatch, source);

            observable.connect();
            return observable;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void accept(Object action) throws Exception {
        dispatch.accept(action);
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        observable.subscribe(observer);
    }

    @Override
    public T call() throws Exception {
        return getState.call();
    }

}
