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

import io.reactivex.Emitter;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

import java.util.concurrent.Callable;

public interface Middleware<T> extends ObservableTransformer<MiddlewareParams<T>, Object> {

    interface Map<T> extends BiFunction<Callable<T>, Object, Object> {
        Object apply(Callable<T> getState, Object action) throws Exception;
    }

    interface EmitComplete<T> extends BiConsumer<MiddlewareParams<T>, Consumer<Object>> {
        void accept(MiddlewareParams<T> params, Consumer<Object> emitter) throws Exception;
    }

    interface Emit<T> extends BiConsumer<MiddlewareParams<T>, Emitter<Object>> {
        void accept(MiddlewareParams<T> params, Emitter<Object> emitter) throws Exception;
    }

    interface FlatMap<T> extends BiFunction<Callable<T>, Object, ObservableSource<Object>> {
        ObservableSource<Object> apply(Callable<T> getState, Object action) throws Exception;
    }

    interface FlatMapIterable<T> extends BiFunction<Callable<T>, Object, Iterable<Object>> {
        Iterable<Object> apply(Callable<T> getState, Object action) throws Exception;
    }

    interface None<T> extends BiConsumer<Callable<T>, Object> {
        void accept(Callable<T> getState, Object action) throws Exception;
    }

    interface Just<T> extends BiConsumer<Callable<T>, Object> {
        void accept(Callable<T> getState, Object action) throws Exception;
    }

}
