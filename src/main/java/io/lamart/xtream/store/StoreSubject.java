package io.lamart.xtream.store;

import io.lamart.xtream.middleware.MiddlewareParams;
import io.lamart.xtream.state.AtomicState;
import io.lamart.xtream.state.State;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.functions.BiFunction;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public abstract class StoreSubject<T> extends Store<T> implements ObservableTransformer<Object, T> {

    protected final State<T> state;
    protected final Subject<Object> subject;
    protected final Observable<T> observable;

    public StoreSubject(T initialValue) {
        this(new AtomicState<T>(initialValue), PublishSubject.create());
    }

    public StoreSubject(State<T> state, Subject<Object> subject) {
        this.state = state;
        this.subject = subject;
        this.observable = subject.compose(this);
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        observable.subscribe(observer);
    }

    @Override
    public void accept(Object action) throws Exception {
        subject.onNext(action);
    }

    public static <T> StoreSubject<T> fromSource(T initialValue, final StoreSource<T> source) {
        return new StoreSubject<T>(initialValue) {
            @Override
            public ObservableSource<T> apply(Observable<Object> upstream) {
                return upstream.compose(StoreTransformer.fromSource(state, this, source));
            }
        };
    }

    public static <T> StoreSubject<T> fromReducer(T initialValue, final BiFunction<T, Object, T> reducer) {
        return new StoreSubject<T>(initialValue) {
            @Override
            public ObservableSource<T> apply(Observable<Object> upstream) {
                return upstream.compose(StoreTransformer.fromReducer(state, reducer));
            }
        };
    }

    public static <T> StoreSubject<T> fromMiddleware(T initialValue, final ObservableTransformer<MiddlewareParams<T>, Object> middleware) {
        return new StoreSubject<T>(initialValue) {
            @Override
            public ObservableSource<T> apply(Observable<Object> upstream) {
                return upstream.compose(StoreTransformer.fromMiddleware(state, this, middleware));
            }
        };
    }

    public static <T> StoreSubject<T> from(T initialValue, final ObservableTransformer<MiddlewareParams<T>, Object> middleware, final BiFunction<T, Object, T> reducer) {
        return new StoreSubject<T>(initialValue) {
            @Override
            public ObservableSource<T> apply(Observable<Object> upstream) {
                return upstream.compose(StoreTransformer.from(state, this, middleware, reducer));
            }
        };
    }

    @Override
    public T call() throws Exception {
        return state.call();
    }

}
