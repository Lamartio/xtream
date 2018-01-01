package io.lamart.xtream.store;

import io.lamart.xtream.state.State;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.observables.ConnectableObservable;

public interface StoreInitializer<T> extends Function3<State<T>, Consumer<Object>, Observable<Object>, ConnectableObservable<T>> {

    @Override
    ConnectableObservable<T> apply(State<T> state, Consumer<Object> dispatch, Observable<Object> observable) throws Exception;

}
