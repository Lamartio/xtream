package io.lamart.xtream.store;

import io.lamart.xtream.middleware.Middleware;
import io.lamart.xtream.middleware.MiddlewareUtil;
import io.lamart.xtream.reducer.Reducer;
import io.lamart.xtream.reducer.ReducerUtil;

public interface StoreSource<T> {

    Middleware<T> getMiddleware();

    Reducer<T> getReducer();

    class Instance<T> implements StoreSource<T> {

        private final Middleware<T> middleware;
        private final Reducer<T> reducer;

        public Instance() {
            this(MiddlewareUtil.<T>newDefaultInstance(), ReducerUtil.<T>newDefaultInstance());
        }

        public Instance(Middleware<T> middleware, Reducer<T> reducer) {
            this.middleware = middleware;
            this.reducer = reducer;
        }

        @Override
        public Middleware<T> getMiddleware() {
            return middleware;
        }

        @Override
        public Reducer<T> getReducer() {
            return reducer;
        }
    }

}
