import io.lamart.xtream.reducer.Reducer;
import io.lamart.xtream.reducer.ReducerParams;
import io.lamart.xtream.reducer.ReducerTransformer;
import io.lamart.xtream.state.AtomicState;
import io.lamart.xtream.state.State;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import org.junit.Test;

public class ReducerTransformerTests {

    private final Reducer<Integer> incrementReducer = (state, action) -> state + 1;

    @Test
    public void withReducerParams() {
        final State<Integer> state = new AtomicState<>(0);
        final ObservableTransformer<ReducerParams<Integer>, Integer> transformer
                = ReducerTransformer.from(incrementReducer);

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
                = ReducerTransformer.from(state, incrementReducer);

        Observable
                .just("a", "b", "c")
                .compose(transformer)
                .test()
                .assertValues(1, 2, 3)
                .assertNoErrors()
                .assertComplete();
    }
}
