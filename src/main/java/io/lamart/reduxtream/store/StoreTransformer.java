package io.lamart.reduxtream.store;

import io.reactivex.ObservableTransformer;

public interface StoreTransformer<T> extends ObservableTransformer<Object, T> {
}
