package io.lamart.reduxtream;


import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiFunction;

final class ReduceTransformer<T> implements ObservableTransformer<Object, T> {


    private final T initialValue;
    private final Iterable<BiFunction<T, Object, T>> reducers;

    ReduceTransformer(T initialValue, Iterable<BiFunction<T, Object, T>> reducers) {
        this.initialValue = initialValue;
        this.reducers = reducers;
    }

    @Override
    public ObservableSource<T> apply(Observable<Object> upstream) {
        return upstream.scan(initialValue, new BiFunction<T, Object, T>() {
            @Override
            public T apply(T t, Object o) throws Exception {
                return null;
            }
        });
    }
}
