package io.lamart.xtream.middleware;

import io.lamart.xtream.reducer.ReducerTransformerParams;
import io.lamart.xtream.state.State;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;

public final class MiddlewareTransformer<T> implements ObservableTransformer<MiddlewareParams<T>, ReducerTransformerParams<T>> {

    private final Middleware<T> middleware;

    private MiddlewareTransformer(Middleware<T> middleware) {
        this.middleware = middleware;
    }

    public static <T> MiddlewareTransformer<T> from(Middleware<T> middleware) {
        return new MiddlewareTransformer<T>(middleware);
    }

    public static <T> ObservableTransformer<Object, ReducerTransformerParams<T>> from(final State<T> state, final Middleware<T> middleware) {
        return new ObservableTransformer<Object, ReducerTransformerParams<T>>() {
            @Override
            public ObservableSource<ReducerTransformerParams<T>> apply(Observable<Object> observable) {
                return observable
                        .map(MiddlewareParams.map(state))
                        .compose(new MiddlewareTransformer<T>(middleware));
            }
        };
    }

    @Override
    public ObservableSource<ReducerTransformerParams<T>> apply(Observable<MiddlewareParams<T>> observable) {
        return observable.flatMap(new Function<MiddlewareParams<T>, ObservableSource<ReducerTransformerParams<T>>>() {
            @Override
            public ObservableSource<ReducerTransformerParams<T>> apply(MiddlewareParams<T> params) throws Exception {
                return Observable
                        .just(params)
                        .compose(middleware)
                        .map(ReducerTransformerParams.map(params.state));
            }
        });
    }
}
