package io.lamart.xtream.store;

import io.lamart.xtream.middleware.Middleware;
import io.lamart.xtream.middleware.MiddlewareUtil;
import io.lamart.xtream.reducer.Reducer;
import io.lamart.xtream.reducer.ReducerUtil;

public interface StoreSource<T> {

    Middleware<T> getMiddleware();

    Reducer<T> getReducer();

    class Instance<T> implements StoreSource<T> {

        private Middleware<T> middleware = MiddlewareUtil.newDefaultInstance();
        private Reducer<T> reducer = ReducerUtil.newDefaultInstance();

        @Override
        public Middleware<T> getMiddleware() {
            return middleware;
        }

        @Override
        public Reducer<T> getReducer() {
            return reducer;
        }

        public Instance<T> add(Reducer<T> reducer) {
            this.reducer = ReducerUtil.combine(this.reducer, reducer);
            return this;
        }

        public Instance<T> add(Middleware<T> middleware) {
            this.middleware = MiddlewareUtil.combine(this.middleware, middleware);
            return this;
        }

        public Instance<T> add(StoreSource<T> source) {
            return add(source.getMiddleware()).add(source.getReducer());
        }

    }

}
