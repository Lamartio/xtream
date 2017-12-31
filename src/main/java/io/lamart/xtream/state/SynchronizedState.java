package io.lamart.xtream.state;

public class SynchronizedState<T> implements State<T> {

    private T state;

    public SynchronizedState(T initialState) {
        this.state = initialState;
    }

    @Override
    public synchronized void accept(T state) throws Exception {
        this.state = state;
    }

    @Override
    public synchronized T call() throws Exception {
        return state;
    }

}
