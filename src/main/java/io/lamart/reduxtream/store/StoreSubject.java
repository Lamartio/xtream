package io.lamart.reduxtream.store;

import io.lamart.reduxtream.middleware.MiddlewareParams;
import io.lamart.reduxtream.state.AtomicState;
import io.lamart.reduxtream.state.State;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.functions.BiFunction;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public abstract class StoreSubject<T> extends Store<T> implements ObservableTransformer<Object, T> {

    public final State<T> state;
    public final Subject<Object> subject;
    private final Observable<T> observable;
    protected final StoreTransformers storeTransformers = new StoreTransformers();

    public StoreSubject() {
        this(new AtomicState<T>(), PublishSubject.create());
    }

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

    @Override
    public T call() throws Exception {
        return state.call();
    }

    public final class StoreTransformers {

        private StoreTransformers() {
        }

        public StoreTransformer<T> create(ObservableTransformer<MiddlewareParams<T>, Object> middleware) {
            return StoreTransformer.create(state, StoreSubject.this, middleware);
        }

        public StoreTransformer<T> create(BiFunction<T, Object, T> reducer) {
            return StoreTransformer.create(state, reducer);
        }

        public StoreTransformer<T> create(ObservableTransformer<MiddlewareParams<T>, Object> middleware, BiFunction<T, Object, T> reducer) {
            return StoreTransformer.create(state, StoreSubject.this, middleware, reducer);
        }

    }

}
