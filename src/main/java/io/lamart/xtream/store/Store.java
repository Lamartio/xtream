package io.lamart.xtream.store;

import io.lamart.xtream.state.State;
import io.reactivex.Observable;

public abstract class Store<T> extends Observable<T> implements StoreActions<T> {

    protected final State<T> state;

    public Store(State<T> state) {
        this.state = state;
    }

    @Override
    public abstract void accept(Object action) throws Exception;

    @Override
    public T call() throws Exception {
        return state.call();
    }

    public T getState() {
        try {
            return call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void dispatch(Object action) {
        try {
            accept(action);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
