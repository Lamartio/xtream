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

package io.lamart.xtream.reducer;

import io.lamart.xtream.middleware.MiddlewareResult;
import io.lamart.xtream.store.StoreParams;
import io.reactivex.functions.Function;

public final class ReducerParams<T> implements StoreParams<T> {

    public final T state;
    public final Object action;

    private ReducerParams(T state, Object action) {
        this.state = state;
        this.action = action;
    }

    @Override
    public T call() throws Exception {
        return state;
    }

    @Override
    public T getState() {
        return state;
    }

    @Override
    public Object getAction() {
        return action;
    }

    public static <T> Function<T, ReducerParams<T>> map(final ReducerParams<T> params) {
        return new Function<T, ReducerParams<T>>() {
            @Override
            public ReducerParams<T> apply(T state) throws Exception {
                return new ReducerParams<T>(state, params.action);
            }
        };
    }

    static <T> Function<MiddlewareResult<T>, ReducerParams<T>> map() {
        return new Function<MiddlewareResult<T>, ReducerParams<T>>() {
            @Override
            public ReducerParams<T> apply(MiddlewareResult<T> params) throws Exception {
                return new ReducerParams<T>(params.call(), params.action);
            }
        };
    }

    public static <T> Function<T, ReducerParams<T>> map(final Object action) {
        return new Function<T, ReducerParams<T>>() {
            @Override
            public ReducerParams<T> apply(T state) throws Exception {
                return new ReducerParams<T>(state, action);
            }
        };
    }
}
