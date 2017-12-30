import io.lamart.reduxtream.middleware.Middleware;
import io.lamart.reduxtream.middleware.MiddlewareUtil;
import io.lamart.reduxtream.reducer.Reducer;
import io.lamart.reduxtream.store.Store;
import io.lamart.reduxtream.store.StoreSubject;
import io.lamart.reduxtream.store.StoreTransformerUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.observers.TestObserver;
import org.junit.Test;


public class StoreSubjectTests {
    private final Store<Integer> store = new StoreSubject<Integer>(1) {

        private final Reducer<Integer> reducer = (state, action) -> {
            if ("increment!".equals(action)) {
                return state + 1;
            } else if ("decrement!".equals(action)) {
                return state - 1;
            } else if ("duplicate!".equals(action)) {
                return state * 2;
            } else {
                return state;
            }
        };

        private final Middleware<Integer> middleware = MiddlewareUtil.map((state, action) -> action + "!");

        @Override
        public ObservableSource<Integer> apply(Observable<Object> observable) {
            return observable.compose(StoreTransformerUtil.compose(state, this, middleware, reducer));
        }

    };

    @Test
    public void store() {
        final TestObserver<Integer> observer = store.test();

        store.dispatch("increment");
        store.dispatch("duplicate");
        store.dispatch("decrement");

        observer.assertValues(2, 4, 3);
        observer.assertNoErrors();
    }

}
