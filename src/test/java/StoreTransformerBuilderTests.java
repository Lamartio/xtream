import io.lamart.xtream.middleware.Middleware;
import io.lamart.xtream.middleware.MiddlewareUtil;
import io.lamart.xtream.reducer.Reducer;
import io.lamart.xtream.state.AtomicState;
import io.lamart.xtream.state.State;
import io.lamart.xtream.store.StoreTransformer;
import io.lamart.xtream.store.StoreTransformerBuilder;
import io.reactivex.Observable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class StoreTransformerBuilderTests {

    private final Reducer<Integer> incrementReducer = (state, action) -> state + 1;
    private final Reducer<Integer> doubleReducer = (state, action) -> state * 2;

    @Test
    public void singleReducer() {
        final State<Integer> state = new AtomicState<>(0);
        final StoreTransformer<Integer> transformer = new StoreTransformerBuilder<Integer>()
                .addReducer(incrementReducer)
                .build(state, Mock.DISPATCH);

        assertTransformer(transformer, 1, 2, 3);
    }

    @Test
    public void doubleReducer() {
        final State<Integer> state = new AtomicState<>(0);
        final StoreTransformer<Integer> transformer = new StoreTransformerBuilder<Integer>()
                .addReducer(incrementReducer)
                .addReducer(doubleReducer)
                .build(state, Mock.DISPATCH);

        assertTransformer(transformer, 2, 6, 14);
    }

    @Test
    public void singleMiddleware() {
        final State<Integer> state = new AtomicState<>(0);
        final List<String> actions = new ArrayList<>();
        final StoreTransformer<Integer> transformer = new StoreTransformerBuilder<Integer>()
                .addMiddleware(newAppendMiddleware(actions, "!"))
                .build(state, Mock.DISPATCH);

        assertTransformer(transformer, 0, 0, 0);
        assertActions(actions, "a!", "b!", "c!");
    }

    @Test
    public void doubleMiddleware() {
        final State<Integer> state = new AtomicState<>(0);
        final List<String> actions = new ArrayList<>();
        final StoreTransformer<Integer> transformer = new StoreTransformerBuilder<Integer>()
                .addMiddleware(newAppendMiddleware(actions, "!"))
                .addMiddleware(newAppendMiddleware(actions, "?"))
                .build(state, Mock.DISPATCH);

        assertTransformer(transformer, 0, 0, 0);
        assertActions(actions, "a!", "a!?", "b!", "b!?", "c!", "c!?");
    }

    @Test
    public void all() {
        final State<Integer> state = new AtomicState<>(0);
        final List<String> actions = new ArrayList<>();
        final StoreTransformerBuilder<Integer> builder = new StoreTransformerBuilder<Integer>()
                .addMiddleware(newAppendMiddleware(actions, "!"))
                .addMiddleware(newAppendMiddleware(actions, "?"))
                .addReducer(incrementReducer)
                .addReducer(doubleReducer);
        final StoreTransformer<Integer> transformer = new StoreTransformerBuilder<Integer>()
                .add(builder)
                .build(state, Mock.DISPATCH);

        assertTransformer(transformer, 2, 6, 14);
        assertActions(actions, "a!", "a!?", "b!", "b!?", "c!", "c!?");
    }

    private void assertTransformer(StoreTransformer<Integer> transformer, Integer... values) {
        Observable
                .just("a", "b", "c")
                .compose(transformer)
                .test()
                .assertValues(values)
                .assertNoErrors()
                .assertComplete();
    }

    private void assertActions(List<String> actions, String... values) {
        for (int i = 0; i < values.length; i++) {
            final String expected = actions.get(i);
            final String actual = values[i];

            assertEquals(expected, actual);
        }
    }

    private Middleware<Integer> newAppendMiddleware(List<String> actions, String append) {
        return MiddlewareUtil.map((state, action) -> {
            final String next = action + append;

            actions.add(next);
            return next;
        });
    }

}
