import io.lamart.xtream.middleware.Middleware;
import io.lamart.xtream.reducer.Reducer;
import io.lamart.xtream.reducer.ReducerUtil;
import io.lamart.xtream.state.State;
import io.lamart.xtream.state.VolatileState;
import io.lamart.xtream.store.StoreTransformer;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class StoreTransformerTests {

    private final Reducer<Integer> incrementReducer = ReducerUtil.map((state, action) -> state + 1);
    private final Consumer<Object> mockDispatch = action -> {
    };

    @Test
    public void reducer() {
        final State<Integer> state = new VolatileState<>(0);
        final StoreTransformer<Integer> transformer = StoreTransformer.fromReducer(state, incrementReducer);

        assertReducer(transformer, 1, 2, 3);
    }

    @Test
    public void middleware() {
        final State<Integer> state = new VolatileState<>(0);
        final List<String> actions = new ArrayList<>();
        final Middleware<Integer> middleware = observable -> observable.map(params -> {
            String action = params.action + "!";

            actions.add(action);
            return action;
        });
        final StoreTransformer<Integer> transformer = StoreTransformer.fromMiddleware(state, middleware);

        assertReducer(transformer, 0, 0, 0);
        assertMiddleware(actions);
    }

    @Test
    public void middlewareAndReducer() {
        final State<Integer> state = new VolatileState<>(0);
        final List<String> actions = new ArrayList<>();
        final Middleware<Integer> middleware = observable -> observable.map(params -> {
            String action = params.action + "!";

            actions.add(action);
            return action;
        });
        final StoreTransformer<Integer> transformer = StoreTransformer.from(state, middleware, incrementReducer);

        assertReducer(transformer, 1, 2, 3);
        assertMiddleware(actions);
    }

    private void assertReducer(StoreTransformer<Integer> transformer, Integer... values) {
        Observable
                .just("a", "b", "c")
                .compose(transformer)
                .test()
                .assertValues(values)
                .assertNoErrors()
                .assertComplete();
    }

    private void assertMiddleware(List<String> actions) {
        assertEquals(actions.get(0), "a!");
        assertEquals(actions.get(1), "b!");
        assertEquals(actions.get(2), "c!");
    }

}
