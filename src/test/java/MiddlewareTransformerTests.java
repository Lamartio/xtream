import io.lamart.xtream.middleware.Middleware;
import io.lamart.xtream.middleware.MiddlewareParams;
import io.lamart.xtream.middleware.MiddlewareTransformer;
import io.lamart.xtream.middleware.MiddlewareUtil;
import io.lamart.xtream.reducer.ReducerTransformerParams;
import io.lamart.xtream.state.State;
import io.lamart.xtream.state.VolatileState;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import org.junit.Test;

public class MiddlewareTransformerTests {

    private final Middleware<Integer> exclamationMiddleware = MiddlewareUtil.map((integer, action) -> action + "!");

    @Test
    public void withMiddlewareParams() {
        final State<Integer> state = new VolatileState<>(0);
        final ObservableTransformer<MiddlewareParams<Integer>, ReducerTransformerParams<Integer>> transformer
                = MiddlewareTransformer.from(exclamationMiddleware);

        Observable
                .just("a", "b", "c")
                .map(MiddlewareParams.map(state, Mock.DISPATCH))
                .compose(transformer)
                .map(params -> params.action)
                .test()
                .assertValues("a!", "b!", "c!")
                .assertNoErrors();
    }

    @Test
    public void withoutMiddlewareParams() {
        final State<Integer> state = new VolatileState<>(0);
        final ObservableTransformer<Object, ReducerTransformerParams<Integer>> transformer = MiddlewareTransformer.from(
                state,
                Mock.DISPATCH,
                exclamationMiddleware
        );

        Observable
                .just("a", "b", "c")
                .compose(transformer)
                .map(params -> params.action)
                .test()
                .assertValues("a!", "b!", "c!")
                .assertNoErrors();
    }

}
