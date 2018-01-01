package io.lamart.xtream.store;

import io.lamart.xtream.DispatchUtil;
import io.lamart.xtream.DispatchWrapper;
import io.lamart.xtream.state.AtomicState;
import io.lamart.xtream.state.State;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import java.util.concurrent.Callable;

public abstract class Store<T> extends Observable<T> implements StoreActions<T> {

    @Override
    public abstract void accept(Object action) throws Exception;

    public T getState() {
        try {
            return call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void dispatch(Object action) {
        try {
            accept(action);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Store<T> fromObservable(final T initialState, final StoreInitializer<T> initializer) {
        return fromObservable(new AtomicState<T>(initialState), initializer);
    }

    public static <T> Store<T> fromObservable(final State<T> state, final StoreInitializer<T> initializer) {
        final DispatchWrapper dispatch = new DispatchWrapper();
        final Observable<Object> source = Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                dispatch.set(emitter);
            }
        });

        return from(state, dispatch, source, initializer);
    }

    public static <T> Store<T> fromSubject(T initialState, final StoreInitializer<T> initializer) {
        return fromSubject(new AtomicState<T>(initialState), PublishSubject.create(), initializer);
    }

    public static <T> Store<T> fromSubject(
            final State<T> state,
            final Subject<Object> subject,
            final StoreInitializer<T> initializer
    ) {
        return from(state, DispatchUtil.from(subject), subject, initializer);
    }

    private static <T> Store<T> from(State<T> state, Consumer<Object> dispatch, Observable<Object> source, StoreInitializer<T> initializer) {
        try {
            final ConnectableObservable<T> observable = initializer.apply(state, dispatch, source);

            observable.connect();
            return from(state, observable, dispatch);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Store<T> from(final Callable<T> getState, final Observable<T> observable, final Consumer<Object> dispatch) {
        return new Store<T>() {
            @Override
            public void accept(Object action) throws Exception {
                dispatch.accept(action);
            }

            @Override
            protected void subscribeActual(Observer<? super T> observer) {
                observable.subscribe(observer);
            }

            @Override
            public T call() throws Exception {
                return getState.call();
            }
        };
    }

}
