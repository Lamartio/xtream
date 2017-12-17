package io.lamart.reduxtream;


import io.reactivex.functions.BiFunction;

public final class Util {

    private Util() {
        throw new Error();
    }

    public static <T> BiFunction<T, Object, T> wrapReducers(final Iterable<BiFunction<T, Object, T>> reducers) {
        return new BiFunction<T, Object, T>() {
            @Override
            public T apply(T state, final Object action) throws Exception {
                for (BiFunction<T, Object, T> reducer : reducers)
                    state = reducer.apply(state, action);

                return state;
            }
        };
    }

}
