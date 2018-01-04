package io.lamart.xtream.store;

import io.lamart.xtream.middleware.Middleware;
import io.lamart.xtream.reducer.Reducer;
import io.lamart.xtream.state.State;
import io.lamart.xtream.state.VolatileState;
import io.lamart.xtream.util.DispatchUtil;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import java.util.concurrent.Callable;

public final class StoreSubject<T> extends StoreImp<T> {

    private StoreSubject(Callable<T> getState, Consumer<Object> dispatch, Observable<T> observable) {
        super(getState, dispatch, observable);
    }

    public static <T> StoreSubject<T> fromMiddleware(T initialState, Middleware<T> middleware) {
        return from(initialState, StoreInitializerUtil.fromMiddleware(middleware));
    }

    public static <T> StoreSubject<T> fromReducer(T initialState, Reducer<T> reducer) {
        return from(initialState, StoreInitializerUtil.fromReducer(reducer));
    }

    public static <T> StoreSubject<T> fromSource(T initialState, StoreSource<T> source) {
        return from(initialState, StoreInitializerUtil.fromSource(source));
    }

    public static <T> StoreSubject<T> from(T initialState, Middleware<T> middleware, Reducer<T> reducer) {
        return from(initialState, StoreInitializerUtil.from(middleware, reducer));
    }

    public static <T> StoreSubject<T> from(T initialState, StoreInitializer<T> initializer) {
        return from(new VolatileState<T>(initialState), PublishSubject.create(), initializer);
    }

    public static <T> StoreSubject<T> from(State<T> state, StoreInitializer<T> initializer) {
        return from(state, PublishSubject.create(), initializer);
    }

    public static <T> StoreSubject<T> from(State<T> state, Subject<Object> subject, StoreInitializer<T> initializer) {
        final Consumer<Object> dispatch = DispatchUtil.from(subject);
        final Observable<T> observable = apply(initializer, state, subject);

        return new StoreSubject<T>(state, dispatch, observable);
    }
}
