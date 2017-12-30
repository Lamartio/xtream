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

    public static <T> StoreTransformer<T> from(
            final State<T> state,
            final Consumer<Object> dispatch,
            final ObservableTransformer<MiddlewareParams<T>, Object> middleware,
            final BiFunction<T, Object, T> reducer
    ) {
        return new StoreTransformer<T>() {
            @Override
            public ObservableSource<T> apply(Observable<Object> observable) {
                return observable
                        .compose(MiddlewareTransformer.from(state, dispatch, middleware))
                        .compose(ReducerTransformer.from(reducer));
            }
        };
    }

    public static <T> StoreTransformer<T> fromReducer(final State<T> state, final BiFunction<T, Object, T> reducer) {
        return new StoreTransformer<T>() {
            @Override
            public ObservableSource<T> apply(Observable<Object> observable) {
                return observable.compose(ReducerTransformer.from(state, reducer));
            }
        };
    }

    public static <T> StoreTransformer<T> fromSource(
            final State<T> state,
            final Consumer<Object> dispatch,
            final StoreSource<T> source
    ) {
        return from(state, dispatch, source.getMiddleware(), source.getReducer());
    }

    public static <T> StoreTransformer<T> fromMiddleware(
            final State<T> state,
            final Consumer<Object> dispatch,
            final ObservableTransformer<MiddlewareParams<T>, Object> middleware
    ) {
        return new StoreTransformer<T>() {
            @Override
            public ObservableSource<T> apply(Observable<Object> observable) {
                return observable
                        .compose(MiddlewareTransformer.from(state, dispatch, middleware))
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
