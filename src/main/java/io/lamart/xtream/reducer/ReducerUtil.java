package io.lamart.xtream.reducer;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import java.util.Arrays;

public final class ReducerUtil {

    private ReducerUtil() {
        throw new Error();
    }

    public static <T> Reducer<T> filter(final Class actionType, final BiFunction<T, Object, T> reducer) {
        return filter(
                new Predicate<Object>() {
                    @Override
                    public boolean test(Object action) throws Exception {
                        return action.getClass().equals(actionType);
                    }
                }
                , reducer
        );
    }

    public static <T> Reducer<T> filter(final Predicate<Object> actionFilter, final BiFunction<T, Object, T> reducer) {
        return new Reducer<T>() {
            @Override
            public T apply(T state, Object action) throws Exception {
                return actionFilter.test(action) ? reducer.apply(state, action) : state;
            }
        };
    }

    public static <T> Reducer<T> just(final BiConsumer<T, Object> reducer) {
        return new Reducer<T>() {
            @Override
            public T apply(T state, Object action) throws Exception {
                reducer.accept(state, action);
                return state;
            }
        };
    }

    public static <T> Reducer<T> wrap(BiFunction<T, Object, T>... reducerArray) {
        return wrap(Arrays.asList(reducerArray));
    }

    public static <T> Reducer<T> wrap(final Iterable<BiFunction<T, Object, T>> reducerIterable) {
        return Observable
                .fromIterable(reducerIterable)
                .map(new Function<BiFunction<T, Object, T>, Reducer<T>>() {
                    @Override
                    public Reducer<T> apply(final BiFunction<T, Object, T> function) throws Exception {
                        return new Reducer<T>() {
                            @Override
                            public T apply(T state, Object action) throws Exception {
                                return function.apply(state, action);
                            }
                        };
                    }
                })
                .reduce(new BiFunction<Reducer<T>, Reducer<T>, Reducer<T>>() {
                    @Override
                    public Reducer<T> apply(final Reducer<T> previous, final Reducer<T> next) throws Exception {
                        return combine(previous, next);
                    }
                })
                .blockingGet(ReducerUtil.<T>newDefaultInstance());
    }

    public static <T> Reducer<T> combine(final BiFunction<T, Object, T> previous, final BiFunction<T, Object, T> next) {
        return new Reducer<T>() {
            @Override
            public T apply(T state, Object action) throws Exception {
                return next.apply(previous.apply(state, action), action);
            }
        };
    }

    public static <T> Reducer<T> newDefaultInstance() {
        return new Reducer<T>() {
            @Override
            public T apply(T state, Object action) throws Exception {
                return state;
            }
        };
    }
}
