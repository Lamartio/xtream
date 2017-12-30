package io.lamart.xtream.reducer;


import io.reactivex.functions.BiFunction;

public interface Reducer<T> extends BiFunction<T, Object, T> {

    @Override
    T apply(T state, Object action) throws Exception;

}
