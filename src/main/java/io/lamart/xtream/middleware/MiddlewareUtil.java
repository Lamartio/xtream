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

import io.reactivex.*;
import io.reactivex.functions.*;

import java.util.Arrays;
import java.util.concurrent.Callable;

public final class MiddlewareUtil {

    private MiddlewareUtil() {
        throw new Error();
    }

    interface None<T> extends BiConsumer<Callable<T>, Object> {
        void accept(Callable<T> getState, Object action) throws Exception;
    }

    public static <T> Middleware<T> none(final BiConsumer<Callable<T>, Object> middleware) {
        return new Middleware<T>() {
            @Override
            public ObservableSource<Object> apply(Observable<MiddlewareParams<T>> upstream) {
                return upstream
                        .flatMapCompletable(new Function<MiddlewareParams<T>, CompletableSource>() {
                            @Override
                            public CompletableSource apply(final MiddlewareParams<T> params) {
                                return Completable.fromAction(new Action() {
                                    @Override
                                    public void run() throws Exception {
                                        middleware.accept(params, params.action);
                                    }
                                });
                            }
                        })
                        .toObservable();
            }
        };
    }

    interface Map<T> extends BiFunction<Callable<T>, Object, Object> {
        Object apply(Callable<T> getState, Object action) throws Exception;
    }

    public static <T> Middleware<T> map(final BiFunction<Callable<T>, Object, Object> middleware) {
        return new Middleware<T>() {
            @Override
            public ObservableSource<Object> apply(Observable<MiddlewareParams<T>> upstream) {
                return upstream.map(new Function<MiddlewareParams<T>, Object>() {
                    @Override
                    public Object apply(MiddlewareParams<T> params) throws Exception {
                        return middleware.apply(params, params.action);
                    }
                });
            }
        };
    }

    interface Just<T> extends BiConsumer<Callable<T>, Object> {
        void accept(Callable<T> getState, Object action) throws Exception;
    }

    public static <T> Middleware<T> just(final BiConsumer<Callable<T>, Object> middleware) {
        return new Middleware<T>() {
            @Override
            public ObservableSource<Object> apply(Observable<MiddlewareParams<T>> upstream) {
                return upstream
                        .doOnNext(new Consumer<MiddlewareParams<T>>() {

                            @Override
                            public void accept(MiddlewareParams<T> params) throws Exception {
                                middleware.accept(params, params.action);
                            }
                        })
                        .map(new Function<MiddlewareParams<T>, Object>() {
                            @Override
                            public Object apply(MiddlewareParams<T> params) {
                                return params.action;
                            }
                        });
            }
        };
    }

    interface EmitComplete<T> extends BiConsumer<MiddlewareParams<T>, Consumer<Object>> {
        void accept(MiddlewareParams<T> params, Consumer<Object> emitter) throws Exception;
    }

    public static <T> Middleware<T> emitComplete(final BiConsumer<MiddlewareParams<T>, Consumer<Object>> middleware) {
        return emit(new BiConsumer<MiddlewareParams<T>, Emitter<Object>>() {
            @Override
            public void accept(MiddlewareParams<T> params, final Emitter<Object> emitter) throws Exception {
                try {
                    middleware.accept(params, new Consumer<Object>() {
                        @Override
                        public void accept(Object action) {
                            emitter.onNext(action);
                        }
                    });
                } finally {
                    emitter.onComplete();
                }
            }
        });
    }

    interface Emit<T> extends BiConsumer<MiddlewareParams<T>, Emitter<Object>> {
        void accept(MiddlewareParams<T> params, Emitter<Object> emitter) throws Exception;
    }

    public static <T> Middleware<T> emit(final BiConsumer<MiddlewareParams<T>, Emitter<Object>> middleware) {
        return new Middleware<T>() {
            @Override
            public ObservableSource<Object> apply(final Observable<MiddlewareParams<T>> upstream) {
                return upstream.flatMap(new Function<MiddlewareParams<T>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(final MiddlewareParams<T> params) {
                        return Observable.create(new ObservableOnSubscribe<Object>() {
                            @Override
                            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                                middleware.accept(params, emitter);
                            }
                        });
                    }
                });
            }
        };
    }

    interface FlatMap<T> extends BiFunction<Callable<T>, Object, ObservableSource<Object>> {
        ObservableSource<Object> apply(Callable<T> getState, Object action) throws Exception;
    }

    public static <T> Middleware<T> flatMap(final BiFunction<Callable<T>, Object, ObservableSource<Object>> middleware) {
        return new Middleware<T>() {
            @Override
            public ObservableSource<Object> apply(Observable<MiddlewareParams<T>> upstream) {
                return upstream.flatMap(new Function<MiddlewareParams<T>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(MiddlewareParams<T> params) throws Exception {
                        return middleware.apply(params, params.action);
                    }
                });
            }
        };
    }

    interface FlatMapIterable<T> extends BiFunction<Callable<T>, Object, Iterable<Object>> {
        Iterable<Object> apply(Callable<T> getState, Object action) throws Exception;
    }

    public static <T> Middleware<T> flatMapIterable(final BiFunction<Callable<T>, Object, Iterable<Object>> middleware) {
        return new Middleware<T>() {
            @Override
            public ObservableSource<Object> apply(Observable<MiddlewareParams<T>> upstream) {
                return upstream.flatMapIterable(new Function<MiddlewareParams<T>, Iterable<?>>() {
                    @Override
                    public Iterable<?> apply(MiddlewareParams<T> params) throws Exception {
                        return middleware.apply(params, params.action);
                    }
                });
            }
        };
    }

    public static <T> Middleware<T> wrap(Middleware<T>... middleware) {
        return wrap(Arrays.asList(middleware));
    }

    public static <T> Middleware<T> wrap(final Iterable<Middleware<T>> middleware) {
        return Observable
                .fromIterable(middleware)
                .reduce(new BiFunction<Middleware<T>, Middleware<T>, Middleware<T>>() {
                    @Override
                    public Middleware<T> apply(Middleware<T> previous, Middleware<T> next) {
                        return combine(previous, next);
                    }
                })
                .blockingGet(MiddlewareUtil.<T>newDefaultInstance());
    }

    public static <T> Middleware<T> combine(final ObservableTransformer<MiddlewareParams<T>, Object> previous, final ObservableTransformer<MiddlewareParams<T>, Object> next) {
        return new Middleware<T>() {

            @Override
            public ObservableSource<Object> apply(final Observable<MiddlewareParams<T>> upstream) {
                return upstream.flatMap(new Function<MiddlewareParams<T>, ObservableSource<Object>>() {
                    @Override
                    public ObservableSource<Object> apply(final MiddlewareParams<T> params) {
                        return Observable
                                .just(params)
                                .compose(previous)
                                .map(MiddlewareParams.map(params))
                                .compose(next);
                    }
                });
            }
        };
    }

    public static <T> Middleware<T> newDefaultInstance() {
        return new Middleware<T>() {
            @Override
            public ObservableSource<Object> apply(Observable<MiddlewareParams<T>> observable) {
                return observable.map(new Function<MiddlewareParams<T>, Object>() {
                    @Override
                    public Object apply(MiddlewareParams<T> params) {
                        return params.action;
                    }
                });
            }
        };
    }
}
