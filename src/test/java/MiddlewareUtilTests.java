import io.lamart.reduxtream.middleware.Middleware;
import io.lamart.reduxtream.middleware.MiddlewareParams;
import io.lamart.reduxtream.middleware.MiddlewareUtil;
import io.lamart.reduxtream.state.State;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import org.junit.Test;

import java.util.Arrays;

public class MiddlewareUtilTests {

    private final Middleware[] middlewareForWrap = {
            MiddlewareUtil.map((state, action) -> ((Integer) action) + 1),
            MiddlewareUtil.map((state, action) -> ((Integer) action) * 2),
            MiddlewareUtil.map((state, action) -> action.toString())
    };

    @Test
    public void none() {
        final Object[] actions = new Object[]{1, 2, 3};
        final Middleware<Object> middleware = MiddlewareUtil.none((state, action) -> action.toString());

        newActionsObservable(actions)
                .compose(middleware)
                .test()
                .assertNoValues();
    }

    @Test
    public void just() {
        final Object[] actions = new Object[]{1, 2, 3};
        final Middleware<Object> middleware = MiddlewareUtil.just((state, action) -> action.toString());

        newActionsObservable(actions)
                .compose(middleware)
                .test()
                .assertValues(actions);
    }

    @Test
    public void maybe() {
        final ObservableTransformer<MiddlewareParams<Object>, Object> middleware = MiddlewareUtil.maybe((state, action) -> ((Integer) action) % 2 == 0 ? action : null);

        newActionsObservable(1, 2, 3)
                .compose(middleware)
                .test()
                .assertValues(2);
    }

    @Test
    public void map() {
        final ObservableTransformer<MiddlewareParams<Object>, Object> middleware = MiddlewareUtil.map((state, action) -> action.toString());

        newActionsObservable(1, 2, 3)
                .compose(middleware)
                .test()
                .assertValues("1", "2", "3");
    }

    @Test
    public void flatMap() {
        final Middleware<Object> middleware = MiddlewareUtil.flatMap((state, action) -> Arrays.asList(action, action));

        newActionsObservable(1, 2, 3)
                .compose(middleware)
                .test()
                .assertValues(1, 1, 2, 2, 3, 3);
    }

    @Test
    public void combine() {
        final Middleware<Object> middleware1 = MiddlewareUtil.map((Store, action) -> ((Integer) action) * 2);
        final Middleware<Object> middleware2 = MiddlewareUtil.map((Store, action) -> action.toString());
        final ObservableTransformer<MiddlewareParams<Object>, Object> combiner = MiddlewareUtil.combine(middleware1, middleware2);

        newActionsObservable(1, 2, 3)
                .compose(combiner)
                .test()
                .assertValues("2", "4", "6");
    }

    @Test
    public void wrapArray() {
        final Middleware<Object> middleware = MiddlewareUtil.wrap(middlewareForWrap);

        newActionsObservable(1, 2, 3)
                .compose(middleware)
                .test()
                .assertValues("4", "6", "8");
    }

    @Test
    public void wrapIterable() {
        final Middleware<Object> middleware = MiddlewareUtil.wrap(Arrays.asList(middlewareForWrap));

        newActionsObservable(1, 2, 3)
                .compose(middleware)
                .test()
                .assertValues("4", "6", "8");
    }

    private Observable<MiddlewareParams<Object>> newActionsObservable(Object... actions) {
        return Observable
                .fromArray(actions)
                .map(MiddlewareParams.map(new MockState(), null));
    }

    private final class MockState implements State<Object> {

        private final Object state = new Object();

        @Override
        public void accept(Object state) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object call() throws Exception {
            return state;
        }
    }

}
