import io.lamart.xtream.reducer.Reducer;
import io.reactivex.functions.Consumer;

public final class Mock {

    public final static Consumer<Object> DISPATCH = new Consumer<Object>() {
        @Override
        public void accept(Object action) throws Exception {

        }
    };
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
