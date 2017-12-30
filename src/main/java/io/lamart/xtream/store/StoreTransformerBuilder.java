package io.lamart.xtream.store;

import io.lamart.xtream.middleware.Middleware;
import io.lamart.xtream.middleware.MiddlewareParams;
import io.lamart.xtream.middleware.MiddlewareUtil;
import io.lamart.xtream.reducer.Reducer;
import io.lamart.xtream.reducer.ReducerUtil;
import io.lamart.xtream.state.State;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

public class StoreTransformerBuilder<T> implements StoreTransformerBuilderResult<T> {

    private Reducer<T> reducer = ReducerUtil.newDefaultInstance();
    private Middleware<T> middleware = MiddlewareUtil.newDefaultInstance();

    public StoreTransformerBuilder<T> addMiddleware(ObservableTransformer<MiddlewareParams<T>, Object> middleware) {
        this.middleware = MiddlewareUtil.combine(this.middleware, middleware);
        return this;
    }

    public StoreTransformerBuilder<T> addReducer(BiFunction<T, Object, T> reducer) {
        this.reducer = ReducerUtil.combine(this.reducer, reducer);
        return this;
    }

    public StoreTransformerBuilder<T> add(StoreTransformerBuilderResult<T> result) {
        addMiddleware(result.getMiddleware());
        addReducer(result.getReducer());
        return this;
    }

    @Override
    public Middleware<T> getMiddleware() {
        return middleware;
    }

    @Override
    public Reducer<T> getReducer() {
        return reducer;
    }

    public StoreTransformer<T> build(State<T> state, Consumer<Object> dispatch) {
        return StoreTransformer.create(state, dispatch, getMiddleware(), getReducer());
    }

}
