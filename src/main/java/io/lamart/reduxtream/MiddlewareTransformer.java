package io.lamart.reduxtream;


import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

final class MiddlewareTransformer<T> implements ObservableTransformer<Object, Object> {

    private final Iterable<BiFunction<T, Object, ObservableSource<Object>>> middleware;
    private final Callable<T> getState;

    MiddlewareTransformer(Iterable<BiFunction<T, Object, ObservableSource<Object>>> middleware, Callable<T> getState) {
        this.middleware = middleware;
        this.getState = getState;
    }

    @Override
    public ObservableSource<Object> apply(Observable<Object> upstream) {
        return upstream.flatMap(new Function<Object, ObservableSource<Object>>() {
            @Override
            public ObservableSource<Object> apply(Object action) throws Exception {
                return scanMiddlewares(action);
            }
        });
    }

    private Observable<Object> scanMiddlewares(Object action) {
        return Observable
                .fromIterable(middleware)
                .scan(action, new BiFunction<Object, BiFunction<T, Object, ObservableSource<Object>>, Object>() {
                    @Override
                    public Object apply(Object action, BiFunction<T, Object, ObservableSource<Object>> middleware) throws Exception {
                        final T state = getState.call();
                        return middleware.apply(state, action);
                    }
                });
    }
}
