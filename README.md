# xtream
Redux implementation based on ReactiveX.

```java
    public void newStore() {
        final AppState state = new AppState();
        final Middleware<AppState> middleware = new AppMiddleware();
        final Reducer<AppState> reducer = new AppReducer();
        Store<AppState> store = StoreSubject.from(state, middleware, reducer);

        store.dispatch("this");
        store.dispatch("is");
        store.dispatch("an");
        store.dispatch("action");
    }
```
