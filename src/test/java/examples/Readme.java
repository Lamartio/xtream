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

package examples;

import io.lamart.xtream.middleware.Middleware;
import io.lamart.xtream.middleware.MiddlewareParams;
import io.lamart.xtream.middleware.MiddlewareTransformer;
import io.lamart.xtream.reducer.Reducer;
import io.lamart.xtream.reducer.ReducerParams;
import io.lamart.xtream.reducer.ReducerTransformer;
import io.lamart.xtream.reducer.ReducerUtil;
import io.lamart.xtream.state.State;
import io.lamart.xtream.store.Store;
import io.lamart.xtream.store.StoreInitializer;
import io.lamart.xtream.store.StoreSubject;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;

public final class Readme {

    private void newStore() {
        AppState state = new AppState();
        Middleware<AppState> middleware = newMiddleware();
        Reducer<AppState> reducer = newReducer();
        Store<AppState> store = StoreSubject.from(state, middleware, reducer);

        store.subscribe(new Consumer<AppState>() {
            @Override
            public void accept(AppState appState) {
                // for example; update the UI
            }
        });

        store.dispatch("this");
        store.dispatch("is");
        store.dispatch("an");
        store.dispatch("action");
    }

    private void newAdvancedStore() {
        AppState state = new AppState();
        Middleware<AppState> middleware = newMiddleware();
        Reducer<AppState> reducer = newReducer();
        Store<AppState> store = StoreSubject.from(state, new StoreInitializer<AppState>() {
            @Override
            public ConnectableObservable<AppState> apply(Observable<Object> observable, State<AppState> state) {
                return observable
                        .observeOn(Schedulers.computation())
                        .compose(MiddlewareTransformer.from(state, middleware))
                        .observeOn(Schedulers.single())
                        .compose(ReducerTransformer.from(reducer))
                        .startWith(Observable.fromCallable(state))
                        .distinctUntilChanged()
                        .replay(1);
            }
        });
    }

    private Middleware<AppState> newMiddleware() {
        return new Middleware<AppState>() {
            @Override
            public ObservableSource<Object> apply(Observable<MiddlewareParams<AppState>> upstream) {
                return upstream
                        .filter(params -> params.action.equals("download"))
                        .flatMap(params -> download(params))
                        .map(result -> (Object) new SuccessAction(result))
                        .onErrorResumeNext(newErrorAction());
            }
        };
    }

    private Function<Throwable, ObservableSource<?>> newErrorAction() {
        return new Function<Throwable, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Throwable throwable) {
                return Observable.just(new ErrorAction(throwable));
            }
        };
    }

    private Reducer<AppState> newReducer() {
        return new Reducer<AppState>() {
            @Override
            public SingleSource<AppState> apply(Single<ReducerParams<AppState>> upstream) {
                return upstream.flatMap(new Function<ReducerParams<AppState>, SingleSource<AppState>>() {
                    @Override
                    public SingleSource<AppState> apply(ReducerParams<AppState> params) {
                        return Single
                                .just(params)
                                .filter(it -> it.action instanceof SuccessAction)
                                .map(it -> (SuccessAction) it.action)
                                .map(action -> new AppState(action.result))
                                .switchIfEmpty(Single.just(params.state));
                    }
                });
            }
        };
    }

    // a simpler version of newReducer() that does the same thing
    private Reducer<AppState> newSimpleReducer() {
        return ReducerUtil.map(new ReducerUtil.Map<AppState>() {
            @Override
            public AppState apply(AppState state, Object action) {
                if (action instanceof SuccessAction) {
                    return new AppState(((SuccessAction) action).result);
                } else {
                    return state;
                }
            }
        });
    }

    private ObservableSource<String> download(MiddlewareParams<AppState> params) {
        return Observable.just("");
    }

    public class SuccessAction {

        public final String result;

        public SuccessAction(String result) {
            this.result = result;
        }

    }

    public class ErrorAction {

        public final Throwable error;

        public ErrorAction(Throwable error) {
            this.error = error;
        }
    }

    private final class AppState {

        private final String result;

        public AppState() {
            this("");
        }

        public AppState(String result) {
            this.result = result;
        }

    }

}
