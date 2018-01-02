import io.lamart.xtream.reducer.Reducer;
import io.lamart.xtream.reducer.ReducerUtil;
import io.reactivex.functions.Consumer;

public final class Mock {

    public final static Consumer<Object> DISPATCH = action -> {
    };

    /**
     * The reducer supports incrementing, decrementing and doubling an integer.
     */

    public final static Reducer<Integer> MATH_REDUCER = ReducerUtil.map((state, action) -> {
        if ("increment".equals(action)) {
            return state + 1;
        } else if ("decrement".equals(action)) {
            return state - 1;
        } else if ("duplicate".equals(action)) {
            return state * 2;
        } else {
            return state;
        }
    });

    /**
     * Same as the math reducer, but now with an exclamation mark appended to the action.
     */

    public final static Reducer<Integer> EXCLAMATION_MATH_REDUCER = ReducerUtil.map((state, action) -> {
        if ("increment!".equals(action)) {
            return state + 1;
        } else if ("decrement!".equals(action)) {
            return state - 1;
        } else if ("duplicate!".equals(action)) {
            return state * 2;
        } else {
            return state;
        }
    });

    private Mock() {
        throw new Error();
    }

}
