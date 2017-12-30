package io.lamart.reduxtream.state;


import java.util.concurrent.Callable;

import io.reactivex.functions.Consumer;

public interface State<T> extends Callable<T>, Consumer<T> {

    @Override
    void accept(T state) throws Exception;

}
