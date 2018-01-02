package io.lamart.xtream.reducer;


import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;

public interface Reducer<T> extends SingleTransformer<ReducerParams<T>, T> {

    @Override
    SingleSource<T> apply(Single<ReducerParams<T>> upstream);

}
