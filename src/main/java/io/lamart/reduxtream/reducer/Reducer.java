package io.lamart.reduxtream.reducer;


import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiFunction;

public interface Reducer<T> extends BiFunction<T, Object, T> {

    @Override
    T apply(T state, Object action) throws Exception;

}
