package io.lamart.reduxtream.reducer;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

import java.util.Arrays;

public final class ReducerUtil {

    private final static Reducer<?> DEFAULT_INSTANCE = new Reducer<Object>() {
        @Override
        public Object apply(Object state, Object action) throws Exception {
            return state;
        }
    };

    private ReducerUtil() {
        throw new Error();
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
                .blockingGet((Reducer<T>) DEFAULT_INSTANCE);
    }

    public static <T> Reducer<T> combine(final BiFunction<T, Object, T> previous, final BiFunction<T, Object, T> next) throws Exception {
        return new Reducer<T>() {
            @Override
            public T apply(T state, Object action) throws Exception {
                return next.apply(previous.apply(state, action), action);
            }
        };
    }

}
