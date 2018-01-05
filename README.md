# Xtream
Uses the powerful functionality of ReactiveX for creating a Redux store. To get started you simply create a store. The store is an Observable with the addition that it can receive actions.

```java
    public void newStore() {
        AppState state = new AppState();
        Middleware<AppState> middleware = new AppMiddleware();
        Reducer<AppState> reducer = new AppReducer();
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
A middleware is an [Observabletransformer](http://reactivex.io/RxJava/javadoc/io/reactivex/ObservableTransformer.html) that has the `action` and a `.getState()` function and returns 0, 1 more actions.

```java
    public Middleware<AppState> newMiddleware() {
        return new Middleware<AppState>() {
            @Override
            public ObservableSource<Object> apply(Observable<MiddlewareParams<AppState>> upstream) {
                return upstream
                        .filter(params -> params.action.equals("download") && !params.getState().isDownloading)
                        .flatMap(params -> download(params))
                        .map(result -> newDownloadResultAction(result))
                        .onErrorResumeNext(newDownloadErrorAction());
            }
        };
    }
```

Creating middleware this way can be complex. For simpler and/or synchronous implementation, please check out the static functions ```MiddlewareUtil```.

## Creating reducers
