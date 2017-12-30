package io.lamart.reduxtream.reducer;

import io.lamart.reduxtream.state.State;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiFunction;

public final class ReducerTransformerUtil  {

    private ReducerTransformerUtil() {
        throw new Error();
    }

    public static <T> ObservableTransformer<ReducerParams<T>, T> compose(BiFunction<T, Object, T> reducer) {
        return new ReducerTransformer<T>(reducer);
    }

    public static <T> ObservableTransformer<Object, T> compose(final State<T> state, final BiFunction<T, Object, T> reducer) {
        return new ObservableTransformer<Object, T>() {
            @Override
            public ObservableSource<T> apply(Observable<Object> observable) {
                return observable
                        .map(ReducerParams.map(state))
                        .compose(new ReducerTransformer<T>(reducer));
            }
        };
    }
}

