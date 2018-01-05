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

import io.lamart.xtream.state.State;
import io.reactivex.*;
import io.reactivex.functions.Function;

public final class ReducerTransformer<T> implements ObservableTransformer<ReducerTransformerParams<T>, T> {

    private final Reducer<T> reducer;

    private ReducerTransformer(Reducer<T> reducer) {
        this.reducer = reducer;
    }

    public static <T> ReducerTransformer<T> from(Reducer<T> reducer) {
        return new ReducerTransformer<T>(reducer);
    }

    public static <T> ObservableTransformer<Object, T> from(final State<T> state, final Reducer<T> reducer) {
        return new ObservableTransformer<Object, T>() {
            @Override
            public ObservableSource<T> apply(Observable<Object> observable) {
                return observable
                        .map(ReducerTransformerParams.map(state))
                        .compose(new ReducerTransformer<T>(reducer));
            }
        };
    }

    @Override
    public ObservableSource<T> apply(Observable<ReducerTransformerParams<T>> observable) {
        return observable.flatMapSingle(new Function<ReducerTransformerParams<T>, SingleSource<T>>() {
            @Override
            public SingleSource<T> apply(final ReducerTransformerParams<T> params) throws Exception {
                return Single
                        .just(params)
                        .map(ReducerParams.<T>map())
                        .compose(reducer)
                        .doOnSuccess(params);
            }
        });
    }
}
