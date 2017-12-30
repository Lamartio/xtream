package io.lamart.xtream.store;

import io.lamart.xtream.middleware.MiddlewareParams;
import io.lamart.xtream.middleware.MiddlewareTransformer;
import io.lamart.xtream.reducer.ReducerTransformer;
import io.lamart.xtream.state.State;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public abstract class StoreTransformer<T> implements ObservableTransformer<Object, T> {

    private StoreTransformer() {
    }

    public static <T> StoreTransformer<T> create(
            final State<T> state,
            final Consumer<Object> dispatch,
            final ObservableTransformer<MiddlewareParams<T>, Object> middleware,
            final BiFunction<T, Object, T> reducer
    ) {
        return new StoreTransformer<T>() {
            @Override
            public ObservableSource<T> apply(Observable<Object> observable) {
                return observable
                        .compose(MiddlewareTransformer.create(state, dispatch, middleware))
                        .compose(ReducerTransformer.create(reducer));
            }
        };
    }

    public static <T> StoreTransformer<T> create(final State<T> state, final BiFunction<T, Object, T> reducer) {
        return new StoreTransformer<T>() {
            @Override
            public ObservableSource<T> apply(Observable<Object> observable) {
                return observable.compose(ReducerTransformer.create(state, reducer));
            }
        };
    }

    public static <T> StoreTransformer<T> create(
            final State<T> state,
            final Consumer<Object> dispatch,
            final ObservableTransformer<MiddlewareParams<T>, Object> middleware
    ) {
        return new StoreTransformer<T>() {
            @Override
            public ObservableSource<T> apply(Observable<Object> observable) {
                return observable
                        .compose(MiddlewareTransformer.create(state, dispatch, middleware))
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
