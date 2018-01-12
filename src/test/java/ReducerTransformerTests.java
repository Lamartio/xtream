/*
 * MIT License
 *
 * Copyright (c) 2018 Danny Lamarti (Lamartio)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import io.lamart.xtream.middleware.MiddlewareResult;
import io.lamart.xtream.reducer.Reducer;
import io.lamart.xtream.reducer.ReducerTransformer;
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
        final ObservableTransformer<MiddlewareResult<Integer>, Integer> transformer
                = ReducerTransformer.from(incrementReducer);

        Observable
                .just("a", "b", "c")
                .map(MiddlewareResult.map(state))
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
