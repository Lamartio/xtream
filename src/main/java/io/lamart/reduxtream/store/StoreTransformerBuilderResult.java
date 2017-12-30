package io.lamart.reduxtream.store;

import io.lamart.reduxtream.middleware.Middleware;
import io.lamart.reduxtream.reducer.Reducer;

public interface StoreTransformerBuilderResult<T> {

    Middleware<T> getMiddleware();

    Reducer<T> getReducer();

}
