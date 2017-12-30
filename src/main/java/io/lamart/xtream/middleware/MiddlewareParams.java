package io.lamart.xtream.middleware;

import io.lamart.xtream.state.State;
import io.lamart.xtream.store.StoreActions;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class MiddlewareParams<T> implements StoreActions<T> {

    final State<T> state;
    private final Consumer<Object> dispatch;
    public final Object action;

    private MiddlewareParams(State<T> state, Consumer<Object> dispatch, Object action) {
        this.state = state;
        this.dispatch = dispatch;
        this.action = action;
    }

    @Override
    public void accept(Object action) throws Exception {
        dispatch.accept(action);
    }

    @Override
    public void dispatch(Object action) {
        try {
            accept(action);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T getState() {
        try {
            return call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T call() throws Exception {
        return state.call();
    }

    public Object getAction() {
        return action;
    }

    public static <T> Function<Object, MiddlewareParams<T>> map(MiddlewareParams<T> params) {
        return map(params.state, params.dispatch);
    }

    public static <T> Function<Object, MiddlewareParams<T>> map(final State<T> state, final Consumer<Object> dispatch) {
        return new Function<Object, MiddlewareParams<T>>() {
            @Override
            public MiddlewareParams<T> apply(Object action) throws Exception {
                return new MiddlewareParams<T>(state, dispatch, action);
            }
        };
    }

}
