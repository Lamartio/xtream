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
import io.lamart.xtream.reducer.ReducerParams;
import io.lamart.xtream.reducer.ReducerTransformer;
import io.lamart.xtream.reducer.ReducerUtil;
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
                .map(MiddlewareResult.map(state))
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
                .map(MiddlewareResult.map(new VolatileState<>(0)))
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
                .map(MiddlewareResult.map(new VolatileState<>(0)))
                .compose(ReducerTransformer.from(incrementReducer))
                .test()
                .assertValue(1)
                .assertNoErrors()
                .assertComplete();
    }

}
