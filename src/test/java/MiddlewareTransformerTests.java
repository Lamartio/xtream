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

import io.lamart.xtream.middleware.*;
import io.lamart.xtream.middleware.MiddlewareUtil;
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
        final ObservableTransformer<MiddlewareParams<Integer>, MiddlewareResult<Integer>> transformer
                = MiddlewareTransformer.from(exclamationMiddleware);

        Observable
                .just("a", "b", "c")
                .map(MiddlewareParams.map(state))
                .compose(transformer)
                .map(params -> params.action)
                .test()
                .assertValues("a!", "b!", "c!")
                .assertNoErrors();
    }

    @Test
    public void withoutMiddlewareParams() {
        final State<Integer> state = new VolatileState<>(0);
        final ObservableTransformer<Object, MiddlewareResult<Integer>> transformer = MiddlewareTransformer.from(
                state,
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
