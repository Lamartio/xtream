# Xtream
Uses the powerful functionality of ReactiveX for creating a Redux store. The store is simply an Observable with the addition that it can receive actions.

```java
public void newStore() {
    AppState state = new AppState();
    Middleware<AppState> middleware = newMiddleware();
    Reducer<AppState> reducer = newReducer();
    Store<AppState> store = StoreSubject.from(state, middleware, reducer);

    store.subscribe(new Consumer<AppState>() {
        @Override
        public void accept(AppState appState) throws Exception {
            // for example; update the UI
        }
    });

    store.observer("this");
    store.observer("is");
    store.observer("an");
    store.observer("action");
}
```
If you want more options, please check the overloads of `StoreSubject.from` or `StoreObservable.from`.

## Creating middleware
A middleware is an [Observabletransformer](http://reactivex.io/RxJava/javadoc/io/reactivex/ObservableTransformer.html) that has the `action` and a `.getState()` as parameters and returns 0, 1 or more actions.

```java
public Middleware<AppState> newMiddleware() {
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
```

Creating middleware this way can be complex. For simpler implementations, please check out the static functions in the ```MiddlewareUtil``` class.

## Creating reducers
A reducer is a [SingleTransformer](http://reactivex.io/RxJava/javadoc/io/reactivex/SingleTransformer.html) that has the `state` and `action` as a parameter and expects a state as its return value. In addition to the reactive way of creating a reducer, the `ReducerUtil` class contains functions for creating more conventional reducers.

```java
private Reducer<AppState> newReducer() {
    return new Reducer<AppState>() {
        @Override
        public SingleSource<AppState> apply(Single<ReducerParams<AppState>> upstream) {
            return upstream.flatMap(new Function<ReducerParams<AppState>, SingleSource<AppState>>() {
                @Override
                public SingleSource<AppState> apply(ReducerParams<AppState> params) throws Exception {
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
    return ReducerUtil.map(new BiFunction<AppState, Object, AppState>() {
        @Override
        public AppState apply(AppState state, Object action) throws Exception {
            if (action instanceof SuccessAction) {
                return new AppState(((SuccessAction) action).result);
            } else {
                return state;
            }
        }
    });
}
```

## Advanced usage
Xtream enables you to hook into every part of it. The example below schedules all middleware on a multithreaded scheduler and passes their actions through a single threaded scheduler to the reducer. Next the `replay()` operator ensures that when an observer subscribes, it always receives the latest state.

```java
public void newAdvancedStore() {
    AppState state = new AppState();
    Middleware<AppState> middleware = newMiddleware();
    Reducer<AppState> reducer = newReducer();
    Store<AppState> store = StoreSubject.from(state, new StoreInitializer<AppState>() {
        @Override
        public ConnectableObservable<AppState> apply(Observable<Object> observable, State<AppState> state) throws Exception {
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
```

## License
```
MIT License

Copyright (c) 2018 Danny Lamarti (Lamartio)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
