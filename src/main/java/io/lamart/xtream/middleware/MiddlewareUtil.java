package io.lamart.xtream.middleware;

import io.lamart.xtream.util.DispatchUtil;
import io.reactivex.*;
import io.reactivex.functions.*;

import java.util.Arrays;
import java.util.concurrent.Callable;

public final class MiddlewareUtil {

    private MiddlewareUtil() {
        throw new Error();
    }

    public static <T> Middleware<T> none(final BiConsumer<Callable<T>, Object> middleware) {
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
                                        middleware.accept(params, params.action);
                                    }
                                });
                            }
                        })
                        .toObservable();
            }
        };
    }

    public static <T> Middleware<T> maybe(final BiFunction<Callable<T>, Object, Object> middleware) {
        return new Middleware<T>() {
            @Override
            public ObservableSource<Object> apply(Observable<MiddlewareParams<T>> upstream) {
                return upstream.flatMapMaybe(new Function<MiddlewareParams<T>, MaybeSource<Object>>() {

                    @Override
                    public MaybeSource<Object> apply(final MiddlewareParams<T> params) throws Exception {
                        return Maybe.fromCallable(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                return middleware.apply(params, params.action);
                            }
                        });
                    }
                });
            }
        };
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
                            public Object apply(MiddlewareParams<T> params) throws Exception {
                                return params.action;
                            }
                        });
            }
        };
    }

    public static <T> Middleware<T> emitComplete(final BiConsumer<MiddlewareParams<T>, Consumer<Object>> middleware) {
        return emit(new BiConsumer<MiddlewareParams<T>, Emitter<Object>>() {
            @Override
            public void accept(MiddlewareParams<T> params, Emitter<Object> emitter) throws Exception {
                try {
                    middleware.accept(params, DispatchUtil.from(emitter));
                } finally {
                    emitter.onComplete();
                }
            }
        });
    }

    public static <T> Middleware<T> emit(final BiConsumer<MiddlewareParams<T>, Emitter<Object>> middleware) {
        return new Middleware<T>() {
            @Override
            public ObservableSource<Object> apply(final Observable<MiddlewareParams<T>> upstream) {
                return upstream.flatMap(new Function<MiddlewareParams<T>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(final MiddlewareParams<T> params) throws Exception {
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

    public static <T> Middleware<T> wrap(Middleware<T>... middlewareArray) {
        return wrap(Arrays.asList(middlewareArray));
    }

    public static <T> Middleware<T> wrap(final Iterable<Middleware<T>> middlewareIterable) {
        return Observable
                .fromIterable(middlewareIterable)
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
