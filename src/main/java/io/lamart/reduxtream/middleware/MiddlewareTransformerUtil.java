package io.lamart.reduxtream.middleware;

import io.lamart.reduxtream.reducer.ReducerParams;
import io.lamart.reduxtream.state.State;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Consumer;

public final class MiddlewareTransformerUtil {

    private MiddlewareTransformerUtil() {
        throw new Error();
    }

    public static <T> ObservableTransformer<MiddlewareParams<T>, ReducerParams<T>> compose(ObservableTransformer<MiddlewareParams<T>, Object> middleware) {
        return new MiddlewareTransformer<T>(middleware);
    }

    public static <T> ObservableTransformer<Object, ReducerParams<T>> compose(final State<T> state, final Consumer<Object> dispatch, final ObservableTransformer<MiddlewareParams<T>, Object> middleware) {
        return new ObservableTransformer<Object, ReducerParams<T>>() {
            @Override
            public ObservableSource<ReducerParams<T>> apply(Observable<Object> observable) {
                return observable
                        .map(MiddlewareParams.map(state, dispatch))
                        .compose(new MiddlewareTransformer<T>(middleware));
            }
        };
    }
}
