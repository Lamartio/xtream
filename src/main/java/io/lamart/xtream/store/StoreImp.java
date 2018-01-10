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

import io.lamart.xtream.state.State;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;

import java.util.concurrent.Callable;

abstract class StoreImp<T> extends Store<T> {

    private final Callable<T> getState;
    private final Observable<T> observable;
    private final Observer<Object> observer;

    StoreImp(Callable<T> getState, Observable<T> observable, Observer<Object> observer) {
        this.getState = getState;
        this.observable = observable;
        this.observer = observer;
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        observable.subscribe(observer);
    }

    @Override
    public T call() throws Exception {
        return getState.call();
    }

    @Override
    public void onSubscribe(Disposable d) {
        observer.onSubscribe(d);
    }

    @Override
    public void onError(Throwable e) {
        observer.onError(e);
    }

    @Override
    public void onComplete() {
        observer.onComplete();
    }

    @Override
    public void onNext(Object action) {
        observer.onNext(action);
    }

    protected static <T> Observable<T> apply(StoreInitializer<T> initializer, State<T> state, Observable<Object> source) {
        try {
            final ConnectableObservable<T> observable = initializer.apply(source, state);

            observable.connect();
            return observable;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
