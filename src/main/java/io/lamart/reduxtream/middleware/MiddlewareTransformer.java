package io.lamart.reduxtream.middleware;

import io.lamart.reduxtream.reducer.ReducerParams;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;

public class MiddlewareTransformer<T> implements ObservableTransformer<MiddlewareParams<T>, ReducerParams<T>> {

    private final ObservableTransformer<MiddlewareParams<T>, Object> middleware;

    public MiddlewareTransformer(ObservableTransformer<MiddlewareParams<T>, Object> middleware) {
        this.middleware = middleware;
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
