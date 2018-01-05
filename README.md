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
