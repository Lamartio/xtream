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

package io.lamart.xtream.middleware;

import io.lamart.xtream.reducer.ReducerTransformerParams;
import io.lamart.xtream.state.State;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;

public final class MiddlewareTransformer<T> implements ObservableTransformer<MiddlewareParams<T>, ReducerTransformerParams<T>> {

    private final Middleware<T> middleware;

    private MiddlewareTransformer(Middleware<T> middleware) {
        this.middleware = middleware;
    }

    public static <T> MiddlewareTransformer<T> from(Middleware<T> middleware) {
        return new MiddlewareTransformer<T>(middleware);
    }

    public static <T> ObservableTransformer<Object, ReducerTransformerParams<T>> from(final State<T> state, final Middleware<T> middleware) {
        return new ObservableTransformer<Object, ReducerTransformerParams<T>>() {
            @Override
            public ObservableSource<ReducerTransformerParams<T>> apply(Observable<Object> observable) {
                return observable
                        .map(MiddlewareParams.map(state))
                        .compose(new MiddlewareTransformer<T>(middleware));
            }
        };
    }

    @Override
    public ObservableSource<ReducerTransformerParams<T>> apply(Observable<MiddlewareParams<T>> observable) {
        return observable.flatMap(new Function<MiddlewareParams<T>, ObservableSource<ReducerTransformerParams<T>>>() {
            @Override
            public ObservableSource<ReducerTransformerParams<T>> apply(MiddlewareParams<T> params) throws Exception {
                return Observable
                        .just(params)
                        .compose(middleware)
                        .map(ReducerTransformerParams.map(params.state));
            }
        });
    }
}
