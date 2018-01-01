package io.lamart.xtream.store.implementation;

import io.lamart.xtream.DispatchWrapper;
import io.lamart.xtream.middleware.MiddlewareParams;
import io.lamart.xtream.state.State;
import io.lamart.xtream.store.Store;
import io.lamart.xtream.store.StoreInitializer;
import io.lamart.xtream.store.StoreInitializerUtil;
import io.lamart.xtream.store.StoreSource;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

import java.util.concurrent.Callable;

public final class StoreObservable<T> extends Instance<T> {

    private StoreObservable(Callable<T> getState, Consumer<Object> dispatch, Observable<T> observable) {
        super(getState, dispatch, observable);
    }

    public static <T> Store<T> fromSource(State<T> state, StoreSource<T> source) {
        return from(state, StoreInitializerUtil.fromSource(source));
    }

    public static <T> Store<T> fromMiddleware(State<T> state, ObservableTransformer<MiddlewareParams<T>, Object> middleware) {
        return from(state, StoreInitializerUtil.fromMiddleware(middleware));
    }

    public static <T> Store<T> fromReducer(State<T> state, BiFunction<T, Object, T> reducer) {
        return from(state, StoreInitializerUtil.fromReducer(reducer));
    }

    public static <T> StoreObservable<T> from(State<T> state, ObservableTransformer<MiddlewareParams<T>, Object> middleware, BiFunction<T, Object, T> reducer) {
        return from(state, StoreInitializerUtil.from(middleware, reducer));
    }

    public static <T> StoreObservable<T> from(State<T> state, final StoreInitializer<T> initializer) {
        final DispatchWrapper dispatch = new DispatchWrapper();
        final Observable<T> observable = apply(
                initializer,
                state,
                dispatch,
                Observable.create(new ObservableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(ObservableEmitter<Object> e) throws Exception {
                        dispatch.set(e);
                    }
                })
        );

        return new StoreObservable<T>(state, dispatch, observable);
    }

}
