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

    public static <T> Reducer<T> filter(final Class actionType, final Reducer<T> reducer) {
        return filter(
                new Predicate<Object>() {
                    @Override
                    public boolean test(Object action) {
                        return action.getClass().equals(actionType);
                    }
                },
                reducer
        );
    }

    public static <T> Reducer<T> filter(final Predicate<Object> actionFilter, final Reducer<T> reducer) {
        return new Reducer<T>() {
            @Override
            public SingleSource<T> apply(Single<ReducerParams<T>> upstream) {
                return upstream.flatMap(new Function<ReducerParams<T>, SingleSource<? extends T>>() {
                    @Override
                    public SingleSource<? extends T> apply(ReducerParams<T> params) throws Exception {
                        return actionFilter.test(params.action)
                                ? Single.just(params).compose(reducer)
                                : Single.just(params.state);
                    }
                });
            }
        };
    }

    public interface Map<T> extends BiFunction<T, Object, T> {
        T apply(T state, Object action) throws Exception;
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

    public interface Just<T> extends BiConsumer<T, Object> {
        void accept(T state, Object action) throws Exception;
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
                            public T apply(ReducerParams<T> state) {
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
                    public Reducer<T> apply(Reducer<T> previous, Reducer<T> next) {
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
                    public SingleSource<T> apply(final ReducerParams<T> params) {
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
                    public T apply(ReducerParams<T> params) {
                        return params.state;
                    }
                });
            }
        };
    }
}
