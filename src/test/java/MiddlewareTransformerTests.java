import io.lamart.reduxtream.middleware.Middleware;
import io.lamart.reduxtream.middleware.MiddlewareParams;
import io.lamart.reduxtream.middleware.MiddlewareTransformer;
import io.lamart.reduxtream.middleware.MiddlewareUtil;
import io.lamart.reduxtream.reducer.ReducerParams;
import io.lamart.reduxtream.state.AtomicState;
import io.lamart.reduxtream.state.State;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import org.junit.Test;

public class MiddlewareTransformerTests {

    private final Middleware<Integer> exclamationMiddleware = MiddlewareUtil.map((integer, action) -> action + "!");

    @Test
    public void withMiddlewareParams() {
        final State<Integer> state = new AtomicState<>(0);
        final ObservableTransformer<MiddlewareParams<Integer>, ReducerParams<Integer>> transformer
                = MiddlewareTransformer.create(exclamationMiddleware);

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
        final State<Integer> state = new AtomicState<>(0);
        final ObservableTransformer<Object, ReducerParams<Integer>> transformer = MiddlewareTransformer.create(
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
