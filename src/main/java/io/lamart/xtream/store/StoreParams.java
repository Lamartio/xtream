package io.lamart.xtream.store;

import java.util.concurrent.Callable;

public interface StoreParams<T> extends Callable<T> {

    T getState();

    Object getAction();

}
