package io.lamart.reduxtream.state;


import java.util.concurrent.atomic.AtomicReference;

public class AtomicState<T> extends AtomicReference<T> implements State<T> {

    public AtomicState() {
    }

    public AtomicState(T initialValue) {
        super(initialValue);
    }

    @Override
    public void accept(T state) throws Exception {
        set(state);
    }

    @Override
    public T call() throws Exception {
        return get();
    }

}
