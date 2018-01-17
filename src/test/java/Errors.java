/*
 * MIT License
 *
 * Copyright (c) 2018 Danny Lamarti (Lamartio)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import io.lamart.xtream.middleware.Middleware;
import io.lamart.xtream.middleware.MiddlewareUtil;
import io.lamart.xtream.reducer.Reducer;
import io.lamart.xtream.reducer.ReducerUtil;
import io.lamart.xtream.store.Store;
import io.lamart.xtream.store.StoreObservable;
import io.reactivex.plugins.RxJavaPlugins;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Errors {


    @Test
    public void middlewareError() {
        final String message = "This is an middleware error";
        final Middleware<Integer> middleware = MiddlewareUtil.just((getState, action) -> {
            throw new RuntimeException(message);
        });
        final Store<Integer> store = StoreObservable.fromMiddleware(1, middleware);

        RxJavaPlugins.setErrorHandler(throwable -> assertEquals(throwable.getCause().getCause().getMessage(), message));
        store.dispatch("test");
        store.dispatch("test");
        store.dispatch("test");
    }

    @Test
    public void reducerError() {
        final String message = "This is a reducer error";
        final Reducer<Integer> reducer = ReducerUtil.just((state, action) -> {
            throw new RuntimeException(message);
        });
        final Store<Integer> store = StoreObservable.fromReducer(1, reducer);

        RxJavaPlugins.setErrorHandler(throwable -> assertEquals(throwable.getCause().getCause().getMessage(), message));
        store.dispatch("test");
        store.dispatch("test");
        store.dispatch("test");
    }
}
