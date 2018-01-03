package io.lamart.xtream.reducer;


import io.reactivex.SingleTransformer;

public interface Reducer<T> extends SingleTransformer<ReducerParams<T>, T> {

}
