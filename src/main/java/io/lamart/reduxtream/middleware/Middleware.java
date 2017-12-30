package io.lamart.reduxtream.middleware;

import io.reactivex.ObservableTransformer;

public interface Middleware<T> extends ObservableTransformer<MiddlewareParams<T>, Object> {

}
