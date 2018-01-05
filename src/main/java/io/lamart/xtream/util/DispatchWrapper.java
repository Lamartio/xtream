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

package io.lamart.xtream.util;

import io.reactivex.ObservableEmitter;
import io.reactivex.Observer;
import io.reactivex.functions.Consumer;

public class DispatchWrapper implements Consumer<Object> {

    private Consumer<Object> dispatch = DispatchUtil.empty();

    @Override
    public void accept(Object action) throws Exception {
        dispatch.accept(action);
    }

    public DispatchWrapper set(Consumer<Object> dispatch) {
        this.dispatch = dispatch;
        return this;
    }

    public DispatchWrapper set(Observer<Object> observer) {
        return set(DispatchUtil.from(observer));
    }

    public DispatchWrapper set(ObservableEmitter<Object> emitter) {
        return set(DispatchUtil.from(emitter));
    }

}
