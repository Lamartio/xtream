import io.lamart.xtream.middleware.Middleware;
import io.lamart.xtream.middleware.MiddlewareUtil;
import io.lamart.xtream.store.Store;
import io.lamart.xtream.store.StoreSubject;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StoreTests {

    private final Middleware<Integer> doMathMiddleware = MiddlewareUtil.emitComplete((params, emitter) -> {
        assertEquals((int) params.getState(), 1);
        emitter.onNext("increment");
        assertEquals((int) params.getState(), 2);
        emitter.onNext("duplicate");
        assertEquals((int) params.getState(), 4);
        emitter.onNext("decrement");
        assertEquals((int) params.getState(), 3);
    });

    @Test
    public void fromSubject() throws Exception {
        final Store<Integer> store = StoreSubject.fromReducer(0, Mock.MATH_REDUCER);
        final TestObserver<Integer> test1 = store.test();

        store.dispatch("increment");
        final TestObserver<Integer> test2 = store.test();
        store.dispatch("increment");
        store.dispatch("increment");

        test1.assertValues(1, 2, 3);
        test2.assertValues(2, 3);
    }

    @Test
    public void middlewareEmissions() {
        final Store<Integer> store = StoreSubject.from(1, doMathMiddleware, Mock.MATH_REDUCER);
        final TestObserver<Integer> observer = store.test();

        store.dispatch("let's do math!");
        observer.assertValues(2, 4, 3).assertNoErrors();
    }

}
