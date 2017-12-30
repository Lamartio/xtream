package io.lamart.xtream.state;


import io.reactivex.functions.Consumer;

import java.util.concurrent.Callable;

public interface State<T> extends Callable<T>, Consumer<T> {

    @Override
    void accept(T state) throws Exception;

}
