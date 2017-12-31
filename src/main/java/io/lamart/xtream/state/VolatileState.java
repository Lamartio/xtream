package io.lamart.xtream.state;

public class VolatileState<T> implements State<T> {

    private volatile T state;

    public VolatileState(T initialState) {
        this.state = initialState;
    }

    @Override
    public void accept(T state) throws Exception {
        this.state = state;
    }

    @Override
    public T call() throws Exception {
        return state;
    }
}
