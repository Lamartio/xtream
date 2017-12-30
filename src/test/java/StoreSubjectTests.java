import io.lamart.reduxtream.middleware.Middleware;
import io.lamart.reduxtream.middleware.MiddlewareUtil;
import io.lamart.reduxtream.store.Store;
import io.lamart.reduxtream.store.StoreSubject;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.observers.TestObserver;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class StoreSubjectTests {

    @Test
    public void reducerOnly() {
        final Store<Integer> store = new StoreSubject<Integer>(1) {

            @Override
            public ObservableSource<Integer> apply(Observable<Object> observable) {
                return observable.compose(storeTransformers.create(Mock.MATH_REDUCER));
            }

        };
        final TestObserver<Integer> observer = store.test();

        store.dispatch("increment");
        store.dispatch("duplicate");
        store.dispatch("decrement");

        observer.assertValues(2, 4, 3);
        observer.assertNoErrors();
    }

    @Test
    public void middlewareOnly() {
        final List<String> actions = new ArrayList<>();
        final Middleware<Integer> middleware = newExclamationMiddleware(actions);
        final Store<Integer> store = new StoreSubject<Integer>(0) {

            @Override
            public ObservableSource<Integer> apply(Observable<Object> observable) {
                return observable.compose(storeTransformers.create(middleware));
            }

        };
        final TestObserver<Integer> observer = store.test();

        store.dispatch("increment");
        store.dispatch("duplicate");
        store.dispatch("decrement");

        observer.assertValues(0, 0, 0).assertNoErrors();
        assertMiddleware(actions, "increment!", "duplicate!", "decrement!");
    }

    @Test
    public void both() {
        final List<String> actions = new ArrayList<>();
        final Middleware<Integer> middleware = newExclamationMiddleware(actions);
        final Store<Integer> store = new StoreSubject<Integer>(1) {

            @Override
            public ObservableSource<Integer> apply(Observable<Object> observable) {
                return observable.compose(storeTransformers.create(middleware, Mock.EXCLAMATION_MATH_REDUCER));
            }

        };
        final TestObserver<Integer> observer = store.test();

        store.dispatch("increment");
        store.dispatch("duplicate");
        store.dispatch("decrement");

        observer.assertValues(2, 4, 3).assertNoErrors();
        assertMiddleware(actions, "increment!", "duplicate!", "decrement!");
    }

    private void assertMiddleware(List<String> actions, String... actuals) {
        for (int i = 0; i < actuals.length; i++) {
            final String expected = actions.get(i);
            final String actual = actuals[i];

            Assert.assertEquals(expected, actual);
        }
    }

    private Middleware<Integer> newExclamationMiddleware(List<String> actions) {
        return MiddlewareUtil.map((state, action) -> {
            String next = action + "!";

            actions.add(next);
            return next;
        });
    }

}
