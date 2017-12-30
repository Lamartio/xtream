package io.lamart.xtream.store;

import io.lamart.xtream.middleware.Middleware;
import io.lamart.xtream.reducer.Reducer;

public interface StoreSource<T> {

    Middleware<T> getMiddleware();

    Reducer<T> getReducer();

}
