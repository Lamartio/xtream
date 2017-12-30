import io.lamart.reduxtream.reducer.Reducer;
import io.lamart.reduxtream.reducer.ReducerParams;
import io.lamart.reduxtream.reducer.ReducerTransformerUtil;
import io.lamart.reduxtream.reducer.ReducerUtil;
import io.lamart.reduxtream.state.AtomicState;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class ReducerUtilTests {

    private final Reducer<Integer> incrementReducer = (integer, action) -> integer + 1;
    private final Reducer<Integer> duplicateReducer = (integer, action) -> integer * 2;
    private final Reducer<Integer> decrementReducer = (integer, action) -> integer - 1;

    @Test
    public void testIncrements() {
        Observable
                .just(1, 2, 3)
                .map(integer -> incrementReducer.apply(integer, null))
                .test()
                .assertValues(2, 3, 4);
    }

    @Test
    public void wrapArray() throws Exception {
        final BiFunction<Integer, Object, Integer> reducer = ReducerUtil.wrap(
                incrementReducer,
                duplicateReducer,
                decrementReducer
        );
        final int result = reducer.apply(1, null);

        Assert.assertEquals(result, 3);
    }

    @Test
    public void wrapIterable() throws Exception {
        final BiFunction<Integer, Object, Integer> reducer = ReducerUtil.wrap(Arrays.asList(
                incrementReducer,
                duplicateReducer,
                decrementReducer
        ));
        final int result = reducer.apply(1, null);

        Assert.assertEquals(result, 3);
    }

    @Test
    public void compose() {
        Observable
                .just("increment")
                .compose(ReducerTransformerUtil.compose(new AtomicState<>(0), incrementReducer))
                .test()
                .assertValue(1)
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void mapAndCompose() {
        Observable
                .just("increment")
                .map(ReducerParams.map(new AtomicState<>(0)))
                .compose(ReducerTransformerUtil.compose(incrementReducer))
                .test()
                .assertValue(1)
                .assertNoErrors()
                .assertComplete();
    }

}
