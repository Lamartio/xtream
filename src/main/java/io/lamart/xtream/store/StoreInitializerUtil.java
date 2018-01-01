package io.lamart.xtream.store;

import io.lamart.xtream.middleware.MiddlewareParams;
import io.lamart.xtream.state.State;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;

public final class StoreInitializerUtil {

    private StoreInitializerUtil() {
        throw new Error();
    }

    public static <T> StoreInitializer<T> fromMiddleware(final ObservableTransformer<MiddlewareParams<T>, Object> middleware) {
        return new StoreInitializer<T>() {
            @Override
            public ConnectableObservable<T> apply(State<T> state, Consumer<Object> dispatch, Observable<Object> observable) throws Exception {
                return observable.compose(StoreTransformer.fromMiddleware(state, dispatch, middleware)).publish();
            }
        };
    }

    public static <T> StoreInitializer<T> fromReducer(final BiFunction<T, Object, T> reducer) {
        return new StoreInitializer<T>() {
            @Override
            public ConnectableObservable<T> apply(State<T> state, Consumer<Object> dispatch, Observable<Object> observable) throws Exception {
                return observable.compose(StoreTransformer.fromReducer(state, reducer)).publish();
            }
        };
    }

    public static <T> StoreInitializer<T> fromSource(final StoreSource<T> source) {
        return new StoreInitializer<T>() {
            @Override
            public ConnectableObservable<T> apply(State<T> state, Consumer<Object> dispatch, Observable<Object> observable) throws Exception {
                return observable.compose(StoreTransformer.fromSource(state, dispatch, source)).publish();
            }
        };
    }

    public static <T> StoreInitializer<T> from(final ObservableTransformer<MiddlewareParams<T>, Object> middleware, final BiFunction<T, Object, T> reducer) {
        return new StoreInitializer<T>() {
            @Override
            public ConnectableObservable<T> apply(State<T> state, Consumer<Object> dispatch, Observable<Object> observable) throws Exception {
                return observable.compose(StoreTransformer.from(state, dispatch, middleware, reducer)).publish();
            }
        };
    }

}
