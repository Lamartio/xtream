import io.lamart.xtream.store.Store;
import io.lamart.xtream.store.StoreObservable;
import io.lamart.xtream.store.StoreSubject;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

public class StoreTests {

    @Test
    public void fromObservable() throws Exception {
        final Store<Integer> store = StoreObservable.fromReducer(0, Mock.MATH_REDUCER);
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
        final Store<Integer> store = StoreSubject.fromReducer(0, Mock.MATH_REDUCER);
        final TestObserver<Integer> test1 = store.test();

        store.dispatch("increment");
        final TestObserver<Integer> test2 = store.test();
        store.dispatch("increment");
        store.dispatch("increment");

        test1.assertValues(1, 2, 3);
        test2.assertValues(2, 3);
    }

}
