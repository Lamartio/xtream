package io.lamart.xtream.middleware;

import io.lamart.xtream.state.State;
import io.lamart.xtream.store.StoreParams;
import io.reactivex.functions.Function;

public class MiddlewareParams<T> implements StoreParams<T> {

    final State<T> state;
    public final Object action;

    private MiddlewareParams(State<T> state, Object action) {
        this.state = state;
        this.action = action;
    }

    public static <T> Function<Object, MiddlewareParams<T>> map(MiddlewareParams<T> params) {
        return map(params.state);
    }

    @Override
    public Object getAction() {
        return action;
    }

    public static <T> Function<Object, MiddlewareParams<T>> map(final State<T> state) {
        return new Function<Object, MiddlewareParams<T>>() {
            @Override
            public MiddlewareParams<T> apply(Object action) throws Exception {
                return new MiddlewareParams<T>(state, action);
            }
        };
    }

    @Override
    public T getState() {
        try {
            return state.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T call() throws Exception {
        return state.call();
    }

}
