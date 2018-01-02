package io.lamart.xtream.store;

import io.lamart.xtream.state.State;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.observables.ConnectableObservable;

public interface StoreInitializer<T> extends BiFunction<Observable<Object>, State<T>, ConnectableObservable<T>> {

    @Override
    ConnectableObservable<T> apply(Observable<Object> observable, State<T> state) throws Exception;

}
