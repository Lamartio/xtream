package io.lamart.xtream.middleware;

import io.reactivex.ObservableTransformer;

public interface Middleware<T> extends ObservableTransformer<MiddlewareParams<T>, Object> {

}
