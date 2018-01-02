package io.lamart.xtream.store;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import java.util.concurrent.Callable;

public abstract class Store<T> extends Observable<T> implements Callable<T>, Consumer<Object> {

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
