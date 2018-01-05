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
import io.lamart.xtream.store.StoreParams;
import io.reactivex.functions.Function;

public class MiddlewareParams<T> implements StoreParams<T> {

    final State<T> state;
    public final Object action;

    private MiddlewareParams(State<T> state, Object action) {
        this.state = state;
        this.action = action;
    }

    public static <T> Function<Object, MiddlewareParams<T>> map(MiddlewareParams<T> params) {
        return map(params.state);
    }

    @Override
    public Object getAction() {
        return action;
    }

    public static <T> Function<Object, MiddlewareParams<T>> map(final State<T> state) {
        return new Function<Object, MiddlewareParams<T>>() {
            @Override
            public MiddlewareParams<T> apply(Object action) throws Exception {
                return new MiddlewareParams<T>(state, action);
            }
        };
    }

    @Override
    public T getState() {
        try {
            return state.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T call() throws Exception {
        return state.call();
    }

}
