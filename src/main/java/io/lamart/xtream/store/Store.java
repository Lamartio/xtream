package io.lamart.xtream.store;

import io.reactivex.Observable;

public abstract class Store<T> extends Observable<T> implements StoreActions<T> {

    @Override
    public abstract void accept(Object action) throws Exception;

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
