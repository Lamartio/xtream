package io.lamart.xtream.reducer;

import io.lamart.xtream.store.StoreParams;
import io.reactivex.functions.Function;

public final class ReducerParams<T> implements StoreParams<T> {

    public final T state;
    public final Object action;

    private ReducerParams(T state, Object action) {
        this.state = state;
        this.action = action;
    }

    @Override
    public T call() throws Exception {
        return state;
    }

    @Override
    public T getState() {
        return state;
    }

    @Override
    public Object getAction() {
        return action;
    }

    public static <T> Function<T, ReducerParams<T>> map(final ReducerParams<T> params) {
        return new Function<T, ReducerParams<T>>() {
            @Override
            public ReducerParams<T> apply(T state) throws Exception {
                return new ReducerParams<T>(state, params.action);
            }
        };
    }

    static <T> Function<ReducerTransformerParams<T>, ReducerParams<T>> map() {
        return new Function<ReducerTransformerParams<T>, ReducerParams<T>>() {
            @Override
            public ReducerParams<T> apply(ReducerTransformerParams<T> params) throws Exception {
                return new ReducerParams<T>(params.call(), params.action);
            }
        };
    }

    public static <T> Function<T, ReducerParams<T>> map(final Object action) {
        return new Function<T, ReducerParams<T>>() {
            @Override
            public ReducerParams<T> apply(T state) throws Exception {
                return new ReducerParams<T>(state, action);
            }
        };
    }
}
