package io.lamart.reduxtream;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

final class StoreTransformer<T> implements ObservableTransformer<Object, T> {

    private volatile T state;
    private final Callable<T> getState = new Callable<T>() {
        @Override
        public T call() throws Exception {
            return StoreTransformer.this.state;
        }
    };
    private final Consumer<T> setState = new Consumer<T>() {
        @Override
        public void accept(T state) throws Exception {
            StoreTransformer.this.state = state;
        }
    };

    private final BiFunction<T, Object, T> reducer;
    private final Iterable<BiFunction<T, Object, ObservableSource<Object>>> middleware;

    public StoreTransformer(
            final T initialState,
            final BiFunction<T, Object, T> reducer,
            final Iterable<BiFunction<T, Object, ObservableSource<Object>>> middleware) {
        this.state = initialState;
        this.reducer = reducer;
        this.middleware = middleware;
    }

    @Override
    public ObservableSource<T> apply(Observable<Object> upstream) {
        return upstream
                .serialize()
                .compose(new MiddlewareTransformer<T>(middleware, getState))
                .scan(state, reducer)
                .doOnNext(setState)
                .startWith(state);
    }

    public T getState() {
        return state;
    }

}
