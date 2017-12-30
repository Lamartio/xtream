package io.lamart.xtream.reducer;

import io.lamart.xtream.state.State;
import io.reactivex.*;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

import java.util.concurrent.Callable;

public final class ReducerTransformer<T> implements ObservableTransformer<ReducerParams<T>, T> {

    private final BiFunction<T, Object, T> reducer;

    private ReducerTransformer(BiFunction<T, Object, T> reducer) {
        this.reducer = reducer;
    }

    public static <T> ReducerTransformer from(BiFunction<T, Object, T> reducer) {
        return new ReducerTransformer<T>(reducer);
    }

    public static <T> ObservableTransformer<Object, T> from(final State<T> state, final BiFunction<T, Object, T> reducer) {
        return new ObservableTransformer<Object, T>() {
            @Override
            public ObservableSource<T> apply(Observable<Object> observable) {
                return observable
                        .map(ReducerParams.map(state))
                        .compose(new ReducerTransformer<T>(reducer));
            }
        };
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
