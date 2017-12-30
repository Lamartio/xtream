package io.lamart.reduxtream.reducer;

import io.lamart.reduxtream.state.State;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import java.util.concurrent.Callable;

public class ReducerParams<T> implements Callable<T>, Consumer<T> {

    private final State<T> state;
    public final Object action;

    private ReducerParams(State<T> state, Object action) {
        this.state = state;
        this.action = action;
    }

    @Override
    public T call() throws Exception {
        return state.call();
    }

    @Override
    public void accept(T state) throws Exception {
        this.state.accept(state);
    }

    public static <T> Function<Object, ReducerParams<T>> map(final State<T> state) {
        return new Function<Object, ReducerParams<T>>() {
            @Override
            public ReducerParams<T> apply(Object action) throws Exception {
                return new ReducerParams<T>(state, action);
            }
        };
    }
}
