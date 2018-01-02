package io.lamart.xtream.reducer;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.*;

import java.util.Arrays;

public final class ReducerUtil {

    private ReducerUtil() {
        throw new Error();
    }

    public static <T> Reducer<T> filter(final Class actionType, final SingleTransformer<ReducerParams<T>, T> reducer) {
        return filter(
                new Predicate<Object>() {
                    @Override
                    public boolean test(Object action) throws Exception {
                        return action.getClass().equals(actionType);
                    }
                },
                reducer
        );
    }

    public static <T> Reducer<T> filter(final Predicate<Object> actionFilter, final SingleTransformer<ReducerParams<T>, T> reducer) {
        return new Reducer<T>() {
            @Override
            public SingleSource<T> apply(Single<ReducerParams<T>> upstream) {
                return upstream.flatMap(new Function<ReducerParams<T>, SingleSource<? extends T>>() {
                    @Override
                    public SingleSource<? extends T> apply(ReducerParams<T> params) throws Exception {
                        return Single
                                .just(params)
                                .filter(new Predicate<ReducerParams<T>>() {
                                    @Override
                                    public boolean test(ReducerParams<T> params) throws Exception {
                                        return actionFilter.test(params.action);
                                    }
                                })
                                .flatMapSingleElement(new Function<ReducerParams<T>, SingleSource<T>>() {
                                    @Override
                                    public SingleSource<T> apply(ReducerParams<T> params) throws Exception {
                                        return Single.just(params).compose(reducer);
                                    }
                                })
                                .switchIfEmpty(Single.just(params.state));
                    }
                });
            }
        };
    }

    public static <T> Reducer<T> map(final BiFunction<T, Object, T> reducer) {
        return new Reducer<T>() {
            @Override
            public SingleSource<T> apply(Single<ReducerParams<T>> upstream) {
                return upstream.map(new Function<ReducerParams<T>, T>() {
                    @Override
                    public T apply(ReducerParams<T> params) throws Exception {
                        return reducer.apply(params.state, params.action);
                    }
                });
            }
        };
    }

    public static <T> Reducer<T> just(final BiConsumer<T, Object> reducer) {
        return new Reducer<T>() {
            @Override
            public SingleSource<T> apply(Single<ReducerParams<T>> upstream) {
                return upstream
                        .doOnSuccess(new Consumer<ReducerParams<T>>() {
                            @Override
                            public void accept(ReducerParams<T> params) throws Exception {
                                reducer.accept(params.state, params.action);
                            }
                        })
                        .map(new Function<ReducerParams<T>, T>() {
                            @Override
                            public T apply(ReducerParams<T> state) throws Exception {
                                return state.state;
                            }
                        });
            }
        };
    }

    public static <T> Reducer<T> wrap(Reducer<T>... reducerArray) {
        return wrap(Arrays.asList(reducerArray));
    }

    public static <T> Reducer<T> wrap(final Iterable<Reducer<T>> reducerIterable) {
        return Observable
                .fromIterable(reducerIterable)
                .reduce(new BiFunction<Reducer<T>, Reducer<T>, Reducer<T>>() {
                    @Override
                    public Reducer<T> apply(final Reducer<T> previous, final Reducer<T> next) throws Exception {
                        return combine(previous, next);
                    }
                })
                .blockingGet(ReducerUtil.<T>newDefaultInstance());
    }

    public static <T> Reducer<T> combine(final SingleTransformer<ReducerParams<T>, T> previous, final SingleTransformer<ReducerParams<T>, T> next) {
        return new Reducer<T>() {
            @Override
            public SingleSource<T> apply(Single<ReducerParams<T>> upstream) {
                return upstream.flatMap(new Function<ReducerParams<T>, SingleSource<? extends T>>() {
                    @Override
                    public SingleSource<T> apply(final ReducerParams<T> params) throws Exception {
                        return Single
                                .just(params)
                                .compose(previous)
                                .map(ReducerParams.map(params))
                                .compose(next);
                    }
                });
            }

        };
    }

    public static <T> Reducer<T> newDefaultInstance() {
        return new Reducer<T>() {
            @Override
            public SingleSource<T> apply(Single<ReducerParams<T>> upstream) {
                return upstream.map(new Function<ReducerParams<T>, T>() {
                    @Override
                    public T apply(ReducerParams<T> params) throws Exception {
                        return params.state;
                    }
                });
            }
        };
    }
}
