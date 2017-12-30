package io.lamart.reduxtream.middleware;

import io.reactivex.*;
import io.reactivex.functions.*;

import java.util.Arrays;
import java.util.concurrent.Callable;

public final class MiddlewareUtil {

    private static final Middleware<?> DEFAULT_INSTANCE = new Middleware<Object>() {
        @Override
        public ObservableSource<Object> apply(Observable<MiddlewareParams<Object>> observable) {
            return observable.map(new Function<MiddlewareParams<Object>, Object>() {
                @Override
                public Object apply(MiddlewareParams<Object> params) throws Exception {
                    return params.action;
                }
            });
        }
    };

    private MiddlewareUtil() {
        throw new Error();
    }

    public static <T> Middleware<T> none(final BiConsumer<T, Object> middleware) {
        return new Middleware<T>() {
            @Override
            public ObservableSource<Object> apply(Observable<MiddlewareParams<T>> upstream) {
                return upstream
                        .flatMapCompletable(new Function<MiddlewareParams<T>, CompletableSource>() {
                            @Override
                            public CompletableSource apply(final MiddlewareParams<T> params) throws Exception {
                                return Completable.fromAction(new Action() {
                                    @Override
                                    public void run() throws Exception {
                                        middleware.accept(params.call(), params.action);
                                    }
                                });
                            }
                        })
                        .toObservable();
            }
        };
    }

    public static <T> Middleware<T> maybe(final BiFunction<T, Object, Object> middleware) {
        return new Middleware<T>() {
            @Override
            public ObservableSource<Object> apply(Observable<MiddlewareParams<T>> upstream) {
                return upstream.flatMapMaybe(new Function<MiddlewareParams<T>, MaybeSource<Object>>() {

                    @Override
                    public MaybeSource<Object> apply(final MiddlewareParams<T> params) throws Exception {
                        return Maybe.fromCallable(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                return middleware.apply(params.call(), params.action);
                            }
                        });
                    }
                });
            }
        };
    }

    public static <T> Middleware<T> map(final BiFunction<T, Object, Object> middleware) {
        return new Middleware<T>() {
            @Override
            public ObservableSource<Object> apply(Observable<MiddlewareParams<T>> upstream) {
                return upstream.map(new Function<MiddlewareParams<T>, Object>() {
                    @Override
                    public Object apply(MiddlewareParams<T> params) throws Exception {
                        return middleware.apply(params.call(), params.action);
                    }
                });
            }
        };
    }

    public static <T> Middleware<T> just(final BiConsumer<T, Object> middleware) {
        return new Middleware<T>() {
            @Override
            public ObservableSource<Object> apply(Observable<MiddlewareParams<T>> upstream) {
                return upstream
                        .doOnNext(new Consumer<MiddlewareParams<T>>() {

                            @Override
                            public void accept(MiddlewareParams<T> params) throws Exception {
                                middleware.accept(params.call(), params.action);
                            }
                        })
                        .map(new Function<MiddlewareParams<T>, Object>() {
                            @Override
                            public Object apply(MiddlewareParams<T> params) throws Exception {
                                return params.action;
                            }
                        });
            }
        };
    }

    public static <T> Middleware<T> wrap(ObservableTransformer<MiddlewareParams<T>, Object>... middlewareArray) {
        return wrap(Arrays.asList(middlewareArray));
    }

    public static <T> Middleware<T> wrap(final Iterable<ObservableTransformer<MiddlewareParams<T>, Object>> middlewareIterable) {
        return Observable
                .fromIterable(middlewareIterable)
                .map(new Function<ObservableTransformer<MiddlewareParams<T>, Object>, Middleware<T>>() {
                    @Override
                    public Middleware<T> apply(final ObservableTransformer<MiddlewareParams<T>, Object> middleware) throws Exception {
                        return new Middleware<T>() {
                            @Override
                            public ObservableSource<Object> apply(Observable<MiddlewareParams<T>> observable) {
                                return middleware.apply(observable);
                            }
                        };
                    }
                })
                .reduce(new BiFunction<Middleware<T>, Middleware<T>, Middleware<T>>() {
                    @Override
                    public Middleware<T> apply(Middleware<T> previous, Middleware<T> next) throws Exception {
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
                    public ObservableSource<Object> apply(final MiddlewareParams<T> params) throws Exception {
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
                    public Object apply(MiddlewareParams<T> params) throws Exception {
                        return params.action;
                    }
                });
            }
        };
    }
}
