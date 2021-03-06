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
import io.lamart.xtream.reducer.Reducer;
import io.lamart.xtream.state.State;
import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;

public final class StoreInitializerUtil {

    private StoreInitializerUtil() {
        throw new Error();
    }

    public static <T> StoreInitializer<T> fromMiddleware(final Middleware<T> middleware) {
        return new StoreInitializer<T>() {
            @Override
            public ConnectableObservable<T> apply(Observable<Object> observable, State<T> state) throws Exception {
                return observable.compose(StoreTransformer.fromMiddleware(state, middleware)).publish();
            }
        };
    }

    public static <T> StoreInitializer<T> fromReducer(final Reducer<T> reducer) {
        return new StoreInitializer<T>() {
            @Override
            public ConnectableObservable<T> apply(Observable<Object> observable, State<T> state) throws Exception {
                return observable.compose(StoreTransformer.fromReducer(state, reducer)).publish();
            }
        };
    }

    public static <T> StoreInitializer<T> fromSource(final StoreSource<T> source) {
        return new StoreInitializer<T>() {
            @Override
            public ConnectableObservable<T> apply(Observable<Object> observable, State<T> state) throws Exception {
                return observable.compose(StoreTransformer.fromSource(state, source)).publish();
            }
        };
    }

    public static <T> StoreInitializer<T> from(final Middleware<T> middleware, final Reducer<T> reducer) {
        return new StoreInitializer<T>() {
            @Override
            public ConnectableObservable<T> apply(Observable<Object> observable, State<T> state) throws Exception {
                return observable.compose(StoreTransformer.from(state, middleware, reducer)).publish();
            }
        };
    }

}
