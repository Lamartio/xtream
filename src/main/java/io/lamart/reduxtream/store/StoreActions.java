package io.lamart.reduxtream.store;

import io.reactivex.functions.Consumer;

import java.util.concurrent.Callable;

public interface StoreActions<T> extends Callable<T>, Consumer<Object> {

    @Override
    void accept(Object action) throws Exception;

    void dispatch(Object action);

    T getState();

}
