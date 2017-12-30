package io.lamart.reduxtream.reducer;

import io.reactivex.*;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

import java.util.concurrent.Callable;

public class ReducerTransformer<T> implements ObservableTransformer<ReducerParams<T>, T> {

    private final BiFunction<T, Object, T> reducer;

    public ReducerTransformer(BiFunction<T, Object, T> reducer) {
        this.reducer = reducer;
    }

    @Override
    public ObservableSource<T> apply(Observable<ReducerParams<T>> observable) {
        return observable.flatMapSingle(new Function<ReducerParams<T>, SingleSource<T>>() {
            @Override
            public SingleSource<T> apply(final ReducerParams<T> params) throws Exception {
                return Single
                        .fromCallable(new Callable<T>() {
                            @Override
                            public T call() throws Exception {
                                return reducer.apply(params.call(), params.action);
                            }
                        })
                        .doOnSuccess(params);
            }
        });
    }
}
