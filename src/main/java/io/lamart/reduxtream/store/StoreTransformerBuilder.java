package io.lamart.reduxtream.store;

import io.lamart.reduxtream.middleware.Middleware;
import io.lamart.reduxtream.middleware.MiddlewareParams;
import io.lamart.reduxtream.middleware.MiddlewareUtil;
import io.lamart.reduxtream.reducer.Reducer;
import io.lamart.reduxtream.reducer.ReducerUtil;
import io.lamart.reduxtream.state.State;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

public class StoreTransformerBuilder<T> implements StoreTransformerBuilderResult<T> {

    private Reducer<T> reducer = ReducerUtil.newDefaultInstance();
    private Middleware<T> middleware = MiddlewareUtil.newDefaultInstance();

    public StoreTransformerBuilder<T> add(ObservableTransformer<MiddlewareParams<T>, Object> middleware) {
        this.middleware = MiddlewareUtil.combine(this.middleware, middleware);
        return this;
    }

    public StoreTransformerBuilder<T> add(BiFunction<T, Object, T> reducer) {
        this.reducer = ReducerUtil.combine(this.reducer, reducer);
        return this;
    }

    public StoreTransformerBuilder<T> add(StoreTransformerBuilderResult<T> result) {
        add(result.getMiddleware());
        add(result.getReducer());
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
        return StoreTransformer.create(state, dispatch, middleware, reducer);
    }

}
