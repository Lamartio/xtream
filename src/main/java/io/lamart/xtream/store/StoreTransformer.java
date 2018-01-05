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

package io.lamart.xtream.store;

import io.lamart.xtream.middleware.Middleware;
import io.lamart.xtream.middleware.MiddlewareTransformer;
import io.lamart.xtream.reducer.Reducer;
import io.lamart.xtream.reducer.ReducerTransformer;
import io.lamart.xtream.state.State;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;

public abstract class StoreTransformer<T> implements ObservableTransformer<Object, T> {

    private StoreTransformer() {
    }

    public static <T> StoreTransformer<T> fromSource(
            final State<T> state,
            final StoreSource<T> source
    ) {
        return from(state, source.getMiddleware(), source.getReducer());
    }

    public static <T> StoreTransformer<T> from(
            final State<T> state,
            final Middleware<T> middleware,
            final Reducer<T> reducer
    ) {
        return new StoreTransformer<T>() {
            @Override
            public ObservableSource<T> apply(Observable<Object> observable) {
                return observable
                        .compose(MiddlewareTransformer.from(state, middleware))
                        .compose(ReducerTransformer.from(reducer));
            }
        };
    }

    public static <T> StoreTransformer<T> fromReducer(final State<T> state, final Reducer<T> reducer) {
        return new StoreTransformer<T>() {
            @Override
            public ObservableSource<T> apply(Observable<Object> observable) {
                return observable.compose(ReducerTransformer.from(state, reducer));
            }
        };
    }

    public static <T> StoreTransformer<T> fromMiddleware(
            final State<T> state,
            final Middleware<T> middleware
    ) {
        return new StoreTransformer<T>() {
            @Override
            public ObservableSource<T> apply(Observable<Object> observable) {
                return observable
                        .compose(MiddlewareTransformer.from(state, middleware))
                        .map(new Function<Object, T>() {
                            @Override
                            public T apply(Object action) throws Exception {
                                return state.call();
                            }
                        });
            }
        };
    }
}
