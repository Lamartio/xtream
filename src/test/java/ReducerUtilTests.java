import io.lamart.xtream.reducer.*;
import io.lamart.xtream.state.State;
import io.lamart.xtream.state.VolatileState;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReducerUtilTests {

    private final Reducer<Integer> incrementReducer = ReducerUtil.map((integer, action) -> integer + 1);
    private final Reducer<Integer> duplicateReducer = ReducerUtil.map((integer, action) -> integer * 2);
    private final Reducer<Integer> decrementReducer = ReducerUtil.map((integer, action) -> integer - 1);

    @Test
    public void testIncrements() {
        Observable
                .just(1, 2, 3)
                .map(ReducerParams.map(""))
                .flatMapSingle(new Function<ReducerParams<Integer>, SingleSource<?>>() {
                    @Override
                    public SingleSource<?> apply(ReducerParams<Integer> params) throws Exception {
                        return Single.just(params).compose(incrementReducer);
                    }
                })
                .test()
                .assertValues(2, 3, 4);
    }


    @Test
    public void filter() {
        final Reducer<Integer> reducer = ReducerUtil.filter(String.class, incrementReducer);
        final State<Integer> state = new VolatileState<>(0);

        Observable
                .just("increment", 0, "increment")
                .map(ReducerTransformerParams.map(state))
                .compose(ReducerTransformer.from(reducer))
                .test()
                .assertValues(1, 1, 2)
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void just() {
        final List<Integer> list = new ArrayList<>();
        final Reducer<Integer> reducer = ReducerUtil.just((state, action) -> list.add(state));

        Observable
                .just(1, 2, 3)
                .map(ReducerParams.map(""))
                .flatMapSingle(new Function<ReducerParams<Integer>, SingleSource<?>>() {
                    @Override
                    public SingleSource<?> apply(ReducerParams<Integer> params) throws Exception {
                        return Single.just(params).compose(reducer);
                    }
                })
                .test()
                .assertValueSequence(list)
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void wrapIterable() throws Exception {
        final Reducer<Integer> reducer = ReducerUtil.wrap(Arrays.asList(
                incrementReducer,
                duplicateReducer,
                decrementReducer
        ));

        Single
                .just(1)
                .map(ReducerParams.map(""))
                .flatMap(new Function<ReducerParams<Integer>, SingleSource<?>>() {
                    @Override
                    public SingleSource<?> apply(ReducerParams<Integer> params) throws Exception {
                        return Single.just(params).compose(reducer);
                    }
                })
                .test()
                .assertValue(3)
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void compose() {
        Observable
                .just("increment")
                .map(ReducerTransformerParams.map(new VolatileState<>(0)))
                .compose(ReducerTransformer.from(new VolatileState<>(0), incrementReducer))
                .test()
                .assertValue(1)
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void mapAndCompose() {
        Observable
                .just("increment")
                .map(ReducerTransformerParams.map(new VolatileState<>(0)))
                .compose(ReducerTransformer.from(incrementReducer))
                .test()
                .assertValue(1)
                .assertNoErrors()
                .assertComplete();
    }

}
