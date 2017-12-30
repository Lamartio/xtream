package io.lamart.xtream.store;

import io.lamart.xtream.state.AtomicState;
import io.lamart.xtream.state.State;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public abstract class StoreSubject<T> extends Store<T> implements ObservableTransformer<Object, T> {

    protected final Subject<Object> subject;
    protected final Observable<T> observable;

    public StoreSubject(T initialValue) {
        this(new AtomicState<T>(initialValue), PublishSubject.create());
    }

    public StoreSubject(T initialValue, Subject<Object> subject) {
        this(new AtomicState<T>(initialValue), subject);
    }

    public StoreSubject(State<T> state, Subject<Object> subject) {
        super(state);
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

}
