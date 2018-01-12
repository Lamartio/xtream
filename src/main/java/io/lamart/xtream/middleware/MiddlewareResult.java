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

package io.lamart.xtream.middleware;

import io.lamart.xtream.state.State;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import java.util.concurrent.Callable;

public class MiddlewareResult<T> implements Callable<T>, Consumer<T> {

    private final State<T> state;
    public final Object action;

    private MiddlewareResult(State<T> state, Object action) {
        this.state = state;
        this.action = action;
    }

    public static <T> Function<Object, MiddlewareResult<T>> map(final State<T> state) {
        return new Function<Object, MiddlewareResult<T>>() {
            @Override
            public MiddlewareResult<T> apply(Object action) throws Exception {
                return new MiddlewareResult<T>(state, action);
            }
        };
    }

    @Override
    public T call() throws Exception {
        return state.call();
    }

    @Override
    public void accept(T state) throws Exception {
        this.state.accept(state);
    }
}
