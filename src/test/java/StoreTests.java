import io.lamart.xtream.store.Store;
import io.lamart.xtream.store.StoreTransformer;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

public class StoreTests {

    @Test
    public void fromObservable() throws Exception {
        final Store<Integer> store = Store.fromObservable(0, (state, dispatch, observable) -> observable
                .compose(StoreTransformer.fromReducer(state, Mock.MATH_REDUCER))
                .publish()
        );
        final TestObserver<Integer> observer1 = store.test();

        store.dispatch("increment");
        final TestObserver<Integer> observer2 = store.test();
        store.dispatch("increment");
        store.dispatch("increment");

        observer1.assertValues(1, 2, 3);
        observer2.assertValues(2, 3);
    }

    @Test
    public void fromSubject() throws Exception {
        final Store<Integer> store = Store.fromSubject(0, (state, dispatch, observable) -> observable
                .compose(StoreTransformer.fromReducer(state, Mock.MATH_REDUCER))
                .publish()
        );
        final TestObserver<Integer> test1 = store.test();

        store.dispatch("increment");
        final TestObserver<Integer> test2 = store.test();
        store.dispatch("increment");
        store.dispatch("increment");

        test1.assertValues(1, 2, 3);
        test2.assertValues(2, 3);
    }

}
