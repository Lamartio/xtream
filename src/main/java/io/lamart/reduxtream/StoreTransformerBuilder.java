package io.lamart.reduxtream;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

public final class StoreTransformerBuilder<T> {

    private final List<BiFunction<T, Object, T>> reducers = new ArrayList<BiFunction<T, Object, T>>();
    private final List<BiFunction<T, Object, ObservableSource<Object>>> middleware = new ArrayList<BiFunction<T, Object, ObservableSource<Object>>>();
    private T initialState;

    public StoreTransformerBuilder<T> setInitialState(T initialState) {
        this.initialState = initialState;
        return this;
    }

    public StoreTransformerBuilder<T> addReducer(BiFunction<T, Object, T> reducer) {
        reducers.add(reducer);
        return this;
    }

    public StoreTransformerBuilder<T> addMiddleware(BiFunction<T, Object, ObservableSource<Object>> middleware) {
        this.middleware.add(middleware);
        return this;
    }

    public StoreTransformerBuilder<T> add(Consumer<StoreTransformerBuilder<T>> block) {
        try {
            block.accept(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    public StoreTransformer build() {
        return new StoreTransformer<T>(initialState, Util.wrapReducers(reducers), middleware);
    }

}