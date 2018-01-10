/*
 * MIT License
 *
 * Copyright (c) 2018 Danny Lamarti (Lamartio)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.lamart.xtream.store;

import io.lamart.xtream.middleware.Middleware;
import io.lamart.xtream.reducer.Reducer;
import io.lamart.xtream.state.State;
import io.lamart.xtream.state.VolatileState;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import java.util.concurrent.Callable;

public final class StoreObservable<T> extends StoreImp<T> {

    public static <T> Store<T> fromSource(T initialState, StoreSource<T> source) {
        return from(initialState, StoreInitializerUtil.fromSource(source));
    }

    public static <T> Store<T> fromMiddleware(T initialState, Middleware<T> middleware) {
        return from(initialState, StoreInitializerUtil.fromMiddleware(middleware));
    }

    public static <T> Store<T> fromReducer(T initialState, Reducer<T> reducer) {
        return from(initialState, StoreInitializerUtil.fromReducer(reducer));
    }

    public static <T> StoreObservable<T> from(T initialState, Middleware<T> middleware, Reducer<T> reducer) {
        return from(initialState, StoreInitializerUtil.from(middleware, reducer));
    }

    public static <T> StoreObservable<T> from(T initialState, final StoreInitializer<T> initializer) {
        return from(new VolatileState<T>(initialState), initializer);
    }

    public static <T> StoreObservable<T> from(State<T> state, final StoreInitializer<T> initializer) {
        final ObserverWrapper<Object> observer = new ObserverWrapper<Object>();
        final Observable<T> observable = apply(
                initializer,
                state,
                Observable.create(new ObservableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(ObservableEmitter<Object> e) throws Exception {
                        observer.set(e);
                    }
                })
        );

        return new StoreObservable<T>(state, observable, observer);
    }

    private StoreObservable(Callable<T> getState, Observable<T> observable, Observer<Object> observer) {
        super(getState, observable, observer);
    }

    private static class ObserverWrapper<T> implements Observer<T> {

        private ObservableEmitter<Object> emitter;

        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onNext(T t) {
            emitter.onNext(t);
        }

        @Override
        public void onError(Throwable e) {
            emitter.onError(e);
        }

        @Override
        public void onComplete() {
            emitter.onComplete();
        }

        void set(ObservableEmitter<Object> emitter) {
            this.emitter = emitter;
        }
    }
}
