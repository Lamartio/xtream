import io.lamart.xtream.reducer.Reducer;
import io.lamart.xtream.reducer.ReducerTransformer;
import io.lamart.xtream.reducer.ReducerTransformerParams;
import io.lamart.xtream.reducer.ReducerUtil;
import io.lamart.xtream.state.State;
import io.lamart.xtream.state.VolatileState;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import org.junit.Test;

public class ReducerTransformerTests {

    private final Reducer<Integer> incrementReducer = ReducerUtil.map((state, action) -> state + 1);

    @Test
    public void withReducerParams() {
        final State<Integer> state = new VolatileState<>(0);
        final ObservableTransformer<ReducerTransformerParams<Integer>, Integer> transformer
                = ReducerTransformer.from(incrementReducer);

        Observable
                .just("a", "b", "c")
                .map(ReducerTransformerParams.map(state))
                .compose(transformer)
                .test()
                .assertValues(1, 2, 3)
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void withoutReducerParams() {
        final State<Integer> state = new VolatileState<>(0);
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
