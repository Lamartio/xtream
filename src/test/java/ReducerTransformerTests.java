import io.lamart.reduxtream.reducer.Reducer;
import io.lamart.reduxtream.reducer.ReducerParams;
import io.lamart.reduxtream.reducer.ReducerTransformer;
import io.lamart.reduxtream.state.AtomicState;
import io.lamart.reduxtream.state.State;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import org.junit.Test;

public class ReducerTransformerTests {

    private final Reducer<Integer> incrementReducer = (state, action) -> state + 1;

    @Test
    public void withReducerParams() {
        final State<Integer> state = new AtomicState<>(0);
        final ObservableTransformer<ReducerParams<Integer>, Integer> transformer
                = ReducerTransformer.create(incrementReducer);

        Observable
                .just("a", "b", "c")
                .map(ReducerParams.map(state))
                .compose(transformer)
                .test()
                .assertValues(1, 2, 3)
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void withoutReducerParams() {
        final State<Integer> state = new AtomicState<>(0);
        final ObservableTransformer<Object, Integer> transformer
                = ReducerTransformer.create(state, incrementReducer);

        Observable
                .just("a", "b", "c")
                .compose(transformer)
                .test()
                .assertValues(1, 2, 3)
                .assertNoErrors()
                .assertComplete();
    }
}
