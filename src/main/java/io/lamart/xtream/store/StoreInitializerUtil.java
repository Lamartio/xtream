package io.lamart.xtream.store;

import io.lamart.xtream.middleware.Middleware;
import io.lamart.xtream.reducer.Reducer;
import io.lamart.xtream.state.State;
import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;

public final class StoreInitializerUtil {

    private StoreInitializerUtil() {
        throw new Error();
    }

    public static <T> StoreInitializer<T> fromMiddleware(final Middleware<T> middleware) {
        return new StoreInitializer<T>() {
            @Override
            public ConnectableObservable<T> apply(Observable<Object> observable, State<T> state) throws Exception {
                return observable.compose(StoreTransformer.fromMiddleware(state, middleware)).publish();
            }
        };
    }

    public static <T> StoreInitializer<T> fromReducer(final Reducer<T> reducer) {
        return new StoreInitializer<T>() {
            @Override
            public ConnectableObservable<T> apply(Observable<Object> observable, State<T> state) throws Exception {
                return observable.compose(StoreTransformer.fromReducer(state, reducer)).publish();
            }
        };
    }

    public static <T> StoreInitializer<T> fromSource(final StoreSource<T> source) {
        return new StoreInitializer<T>() {
            @Override
            public ConnectableObservable<T> apply(Observable<Object> observable, State<T> state) throws Exception {
                return observable.compose(StoreTransformer.fromSource(state, source)).publish();
            }
        };
    }

    public static <T> StoreInitializer<T> from(final Middleware<T> middleware, final Reducer<T> reducer) {
        return new StoreInitializer<T>() {
            @Override
            public ConnectableObservable<T> apply(Observable<Object> observable, State<T> state) throws Exception {
                return observable.compose(StoreTransformer.from(state, middleware, reducer)).publish();
            }
        };
    }

}
