package io.lamart.xtream.middleware;

import io.lamart.xtream.reducer.ReducerParams;
import io.lamart.xtream.state.State;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public final class MiddlewareTransformer<T> implements ObservableTransformer<MiddlewareParams<T>, ReducerParams<T>> {

    private final ObservableTransformer<MiddlewareParams<T>, Object> middleware;

    private MiddlewareTransformer(ObservableTransformer<MiddlewareParams<T>, Object> middleware) {
        this.middleware = middleware;
    }

    public static <T> ObservableTransformer<MiddlewareParams<T>, ReducerParams<T>> from(ObservableTransformer<MiddlewareParams<T>, Object> middleware) {
        return new MiddlewareTransformer<T>(middleware);
    }

    public static <T> ObservableTransformer<Object, ReducerParams<T>> from(final State<T> state, final Consumer<Object> dispatch, final ObservableTransformer<MiddlewareParams<T>, Object> middleware) {
        return new ObservableTransformer<Object, ReducerParams<T>>() {
            @Override
            public ObservableSource<ReducerParams<T>> apply(Observable<Object> observable) {
                return observable
                        .map(MiddlewareParams.map(state, dispatch))
                        .compose(new MiddlewareTransformer<T>(middleware));
            }
        };
    }

    @Override
    public ObservableSource<ReducerParams<T>> apply(Observable<MiddlewareParams<T>> observable) {
        return observable.flatMapSingle(new Function<MiddlewareParams<T>, SingleSource<? extends ReducerParams<T>>>() {
            @Override
            public SingleSource<? extends ReducerParams<T>> apply(MiddlewareParams<T> params) throws Exception {
                return Observable
                        .just(params)
                        .compose(middleware)
                        .map(ReducerParams.map(params.state))
                        .firstOrError();
            }
        });
    }
}
