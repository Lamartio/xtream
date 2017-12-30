package io.lamart.reduxtream.store;

import io.lamart.reduxtream.middleware.MiddlewareParams;
import io.lamart.reduxtream.middleware.MiddlewareTransformerUtil;
import io.lamart.reduxtream.reducer.ReducerTransformerUtil;
import io.lamart.reduxtream.state.State;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public final class StoreTransformerUtil {

    private StoreTransformerUtil() {
        throw new Error();
    }

    public static <T> StoreTransformer<T> compose(
            final State<T> state,
            final Consumer<Object> dispatch,
            final ObservableTransformer<MiddlewareParams<T>, Object> middleware,
            final BiFunction<T, Object, T> reducer
    ) {
        return new StoreTransformer<T>() {
            @Override
            public ObservableSource<T> apply(Observable<Object> observable) {
                return observable
                        .compose(MiddlewareTransformerUtil.compose(state, dispatch, middleware))
                        .compose(ReducerTransformerUtil.compose(reducer));
            }
        };
    }

    public static <T> StoreTransformer<T> compose(final State<T> state, final BiFunction<T, Object, T> reducer) {
        return new StoreTransformer<T>() {
            @Override
            public ObservableSource<T> apply(Observable<Object> observable) {
                return observable.compose(ReducerTransformerUtil.compose(state, reducer));
            }
        };
    }

    public static <T> StoreTransformer<T> compose(
            final State<T> state,
            final Consumer<Object> dispatch,
            final ObservableTransformer<MiddlewareParams<T>, Object> middleware
    ) {
        return new StoreTransformer<T>() {
            @Override
            public ObservableSource<T> apply(Observable<Object> observable) {
                return observable
                        .compose(MiddlewareTransformerUtil.compose(state, dispatch, middleware))
                        .map(new Function<Object, T>() {
                            @Override
                            public T apply(Object action) throws Exception {
                                return state.call();
                            }
                        });
            }
        };
    }
}
