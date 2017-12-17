package io.lamart.reduxtream;


import java.util.AbstractMap;
import java.util.Map;

import io.reactivex.functions.BiFunction;

public final class Util {

    private Util() {
        throw new Error();
    }

    public static <K, V> Map.Entry<K, V> newEntry(K key, V value) {
        return new AbstractMap.SimpleImmutableEntry<K, V>(key, value);
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
