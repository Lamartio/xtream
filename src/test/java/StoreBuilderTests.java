import io.lamart.xtream.state.AtomicState;
import io.lamart.xtream.state.State;
import io.lamart.xtream.store.StoreTransformer;
import io.lamart.xtream.store.StoreTransformerBuilder;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.junit.Test;


public class StoreBuilderTests {

    @Test
    public void empty() {
        final State<Integer> state = new AtomicState<>(0);
        final StoreTransformerBuilder<Integer> builder = new StoreTransformerBuilder<>();
        final Subject<Object> subject = PublishSubject.create();
        final Consumer<Object> dispatch = new Consumer<Object>() {
            @Override
            public void accept(Object action) {
                subject.onNext(action);
            }
        };
        final StoreTransformer<Integer> transformer = builder.build(state, dispatch);
        final Observable<Integer> observable = subject.compose(transformer);
        final TestObserver<Integer> test = observable.test();

        subject.onNext("a");
        subject.onNext("b");
        subject.onNext("c");

        test.assertValues(0, 0, 0).assertNoErrors();
    }

}
