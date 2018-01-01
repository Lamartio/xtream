package io.lamart.xtream.store.implementation;

import io.lamart.xtream.DispatchUtil;
import io.lamart.xtream.middleware.MiddlewareParams;
import io.lamart.xtream.state.State;
import io.lamart.xtream.store.StoreInitializer;
import io.lamart.xtream.store.StoreInitializerUtil;
import io.lamart.xtream.store.StoreSource;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import java.util.concurrent.Callable;

public final class StoreSubject<T> extends Instance<T> {

    private StoreSubject(Callable<T> getState, Consumer<Object> dispatch, Observable<T> observable) {
        super(getState, dispatch, observable);
    }

    public static <T> StoreSubject<T> fromMiddleware(State<T> state, ObservableTransformer<MiddlewareParams<T>, Object> middleware) {
        return from(state, StoreInitializerUtil.fromMiddleware(middleware));
    }

    public static <T> StoreSubject<T> fromReducer(State<T> state, BiFunction<T, Object, T> reducer) {
        return from(state, StoreInitializerUtil.fromReducer(reducer));
    }

    public static <T> StoreSubject<T> fromSource(State<T> state, StoreSource<T> source) {
        return from(state, StoreInitializerUtil.fromSource(source));
    }

    public static <T> StoreSubject<T> from(State<T> state, ObservableTransformer<MiddlewareParams<T>, Object> middleware, BiFunction<T, Object, T> reducer) {
        return from(state, StoreInitializerUtil.from(middleware, reducer));
    }

    public static <T> StoreSubject<T> from(State<T> state, StoreInitializer<T> initializer) {
        return from(state, PublishSubject.create(), initializer);
    }

    public static <T> StoreSubject<T> from(State<T> state, Subject<Object> subject, StoreInitializer<T> initializer) {
        final Consumer<Object> dispatch = DispatchUtil.from(subject);
        final Observable<T> observable = apply(initializer, state, dispatch, subject);

        return new StoreSubject<T>(state, dispatch, observable);
    }
}
