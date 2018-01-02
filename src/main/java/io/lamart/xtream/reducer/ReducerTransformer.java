package io.lamart.xtream.reducer;

import io.lamart.xtream.state.State;
import io.reactivex.*;
import io.reactivex.functions.Function;

public final class ReducerTransformer<T> implements ObservableTransformer<ReducerTransformerParams<T>, T> {

    private final SingleTransformer<ReducerParams<T>, T> reducer;

    private ReducerTransformer(SingleTransformer<ReducerParams<T>, T> reducer) {
        this.reducer = reducer;
    }

    public static <T> ReducerTransformer<T> from(SingleTransformer<ReducerParams<T>, T> reducer) {
        return new ReducerTransformer<T>(reducer);
    }

    public static <T> ObservableTransformer<Object, T> from(final State<T> state, final SingleTransformer<ReducerParams<T>, T> reducer) {
        return new ObservableTransformer<Object, T>() {
            @Override
            public ObservableSource<T> apply(Observable<Object> observable) {
                return observable
                        .map(ReducerTransformerParams.map(state))
                        .compose(new ReducerTransformer<T>(reducer));
            }
        };
    }

    @Override
    public ObservableSource<T> apply(Observable<ReducerTransformerParams<T>> observable) {
        return observable.flatMapSingle(new Function<ReducerTransformerParams<T>, SingleSource<T>>() {
            @Override
            public SingleSource<T> apply(final ReducerTransformerParams<T> params) throws Exception {
                return Single
                        .just(params)
                        .map(ReducerParams.<T>map())
                        .compose(reducer)
                        .doOnSuccess(params);
            }
        });
    }
}
