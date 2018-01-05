# Xtream
Uses the powerful functionality of ReactiveX for creating a Redux store. To get started you simply create a store. The store is an Observable with the addition that it can receive actions.

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

    store.dispatch("this");
    store.dispatch("is");
    store.dispatch("an");
    store.dispatch("action");
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
Xtream enables you to hook in in every part of the framework. The example below schedules all middleware on a multithreaded scheduler and passes their actions through a single threaded scheduler to the reducer. Next the `replay()` operator is used to ensure that when an observer subscribes, it always receives the latest state.

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
                    .replay(1);
        }
    });
}
```
