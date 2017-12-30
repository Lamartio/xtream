import io.lamart.reduxtream.middleware.Middleware;
import io.lamart.reduxtream.middleware.MiddlewareUtil;
import io.lamart.reduxtream.reducer.Reducer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

public final class Mock {

    public final static Consumer<Object> DISPATCH = new Consumer<Object>() {
        @Override
        public void accept(Object action) throws Exception {

        }
    };
    /**
     * Appends an exclamation mark to the action
     */

    public final static Middleware<Integer> EXCLAMATION_MIDDLEWARE = MiddlewareUtil.map(new BiFunction<Integer, Object, Object>() {
        @Override
        public Object apply(Integer state, Object action) throws Exception {
            return action + "!";
        }
    });
    /**
     * The reducer supports incrementing, decrementing and doubling an integer.
     */

    public final static Reducer<Integer> MATH_REDUCER = new Reducer<Integer>() {
        @Override
        public Integer apply(Integer state, Object action) throws Exception {
            if ("increment".equals(action)) {
                return state + 1;
            } else if ("decrement".equals(action)) {
                return state - 1;
            } else if ("duplicate".equals(action)) {
                return state * 2;
            } else {
                return state;
            }
        }
    };
    /**
     * Same as the math reducer, but now with an exclamation mark appended to the action.
     */

    public final static Reducer<Integer> EXCLAMATION_MATH_REDUCER = new Reducer<Integer>() {
        @Override
        public Integer apply(Integer state, Object action) throws Exception {
            if ("increment!".equals(action)) {
                return state + 1;
            } else if ("decrement!".equals(action)) {
                return state - 1;
            } else if ("duplicate!".equals(action)) {
                return state * 2;
            } else {
                return state;
            }
        }
    };

    private Mock() {
        throw new Error();
    }

}
