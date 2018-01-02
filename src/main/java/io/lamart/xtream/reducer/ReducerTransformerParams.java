package io.lamart.xtream.reducer;

import io.lamart.xtream.state.State;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import java.util.concurrent.Callable;

public class ReducerTransformerParams<T> implements Callable<T>, Consumer<T> {

    public final Object action;
    private final State<T> state;

    private ReducerTransformerParams(State<T> state, Object action) {
        this.state = state;
        this.action = action;
    }

    public static <T> Function<Object, ReducerTransformerParams<T>> map(final State<T> state) {
        return new Function<Object, ReducerTransformerParams<T>>() {
            @Override
            public ReducerTransformerParams<T> apply(Object action) throws Exception {
                return new ReducerTransformerParams<T>(state, action);
            }
        };
    }

    @Override
    public T call() throws Exception {
        return state.call();
    }

    @Override
    public void accept(T state) throws Exception {
        this.state.accept(state);
    }
}
