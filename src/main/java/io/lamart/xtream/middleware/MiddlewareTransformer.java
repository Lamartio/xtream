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

import io.lamart.xtream.state.State;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;

public final class MiddlewareTransformer<T> implements ObservableTransformer<MiddlewareParams<T>, MiddlewareResult<T>> {

    private final Middleware<T> middleware;

    private MiddlewareTransformer(Middleware<T> middleware) {
        this.middleware = middleware;
    }

    public static <T> ObservableTransformer<MiddlewareParams<T>, MiddlewareResult<T>> from(Middleware<T> middleware) {
        return new MiddlewareTransformer<T>(middleware);
    }

    public static <T> ObservableTransformer<Object, MiddlewareResult<T>> from(final State<T> state, final Middleware<T> middleware) {
        return new ObservableTransformer<Object, MiddlewareResult<T>>() {
            @Override
            public ObservableSource<MiddlewareResult<T>> apply(Observable<Object> observable) {
                return observable
                        .map(MiddlewareParams.map(state))
                        .compose(new MiddlewareTransformer<T>(middleware));
            }
        };
    }

    @Override
    public ObservableSource<MiddlewareResult<T>> apply(Observable<MiddlewareParams<T>> observable) {
        return observable
                .flatMap(new Function<MiddlewareParams<T>, ObservableSource<MiddlewareResult<T>>>() {
                    @Override
                    public ObservableSource<MiddlewareResult<T>> apply(MiddlewareParams<T> params) throws Exception {
                        return Observable
                                .just(params)
                                .compose(middleware)
                                .map(MiddlewareResult.map(params.state))
                                .doOnError(new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        RxJavaPlugins.onError(new MiddlewareException(throwable));
                                    }
                                })
                                .onErrorResumeNext(Observable.<MiddlewareResult<T>>empty());
                    }
                });
    }
}
