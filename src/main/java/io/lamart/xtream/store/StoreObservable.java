package io.lamart.xtream.store;

import io.lamart.xtream.middleware.Middleware;
import io.lamart.xtream.reducer.ReducerParams;
import io.lamart.xtream.state.State;
import io.lamart.xtream.state.VolatileState;
import io.lamart.xtream.util.DispatchWrapper;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Consumer;

import java.util.concurrent.Callable;

public final class StoreObservable<T> extends StoreImp<T> {

    private StoreObservable(Callable<T> getState, Consumer<Object> dispatch, Observable<T> observable) {
        super(getState, dispatch, observable);
    }

    public static <T> Store<T> fromSource(T initialState, StoreSource<T> source) {
        return from(initialState, StoreInitializerUtil.fromSource(source));
    }

    public static <T> Store<T> fromMiddleware(T initialState, Middleware<T> middleware) {
        return from(initialState, StoreInitializerUtil.fromMiddleware(middleware));
    }

    public static <T> Store<T> fromReducer(T initialState, SingleTransformer<ReducerParams<T>, T> reducer) {
        return from(initialState, StoreInitializerUtil.fromReducer(reducer));
    }

    public static <T> StoreObservable<T> from(T initialState, Middleware<T> middleware, SingleTransformer<ReducerParams<T>, T> reducer) {
        return from(initialState, StoreInitializerUtil.from(middleware, reducer));
    }

    public static <T> StoreObservable<T> from(T initialState, final StoreInitializer<T> initializer) {
        return from(new VolatileState<T>(initialState), initializer);
    }

    public static <T> StoreObservable<T> from(State<T> state, final StoreInitializer<T> initializer) {
        final DispatchWrapper dispatch = new DispatchWrapper();
        final Observable<T> observable = apply(
                initializer,
                state,
                Observable.create(new ObservableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(ObservableEmitter<Object> e) throws Exception {
                        dispatch.set(e);
                    }
                })
        );

        return new StoreObservable<T>(state, dispatch, observable);
    }

}
