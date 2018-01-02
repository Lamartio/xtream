package io.lamart.xtream.store;

import io.lamart.xtream.state.State;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;

import java.util.concurrent.Callable;

abstract class StoreImp<T> extends Store<T> {

    private final Callable<T> getState;
    private final Consumer<Object> dispatch;
    private final Observable<T> observable;

    StoreImp(Callable<T> getState, Consumer<Object> dispatch, Observable<T> observable) {
        this.getState = getState;
        this.dispatch = dispatch;
        this.observable = observable;
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

    protected static <T> Observable<T> apply(StoreInitializer<T> initializer, State<T> state, Observable<Object> source) {
        try {
            final ConnectableObservable<T> observable = initializer.apply(source, state);

            observable.connect();
            return observable;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
