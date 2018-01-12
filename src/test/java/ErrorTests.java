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

import io.lamart.xtream.middleware.Middleware;
import io.lamart.xtream.reducer.Reducer;
import io.lamart.xtream.store.Store;
import io.lamart.xtream.store.StoreObservable;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ErrorTests {


    /**
     * When a Middleware just throws, it just explodes. This is expected behavior from Rx.
     */

    @Test
    public void middleWareThrows() {
        final ArrayList<Throwable> errors = initErrorHandler();
        final Middleware<Integer> middleware = upstream -> {
            throw new RuntimeException("middleware throws!");
        };
        final Store<Integer> store = StoreObservable.fromMiddleware(1, middleware);
        final TestObserver<Integer> observer = store.test();

        store.dispatch("increment");
        store.dispatch("duplicate");
        store.dispatch("decrement");

        observer.assertNoValues().assertError(RuntimeException.class);
        assertEquals(errors.size(), 0);
    }

    /**
     * When there is an error in the stream of a Middleware, it got catched and deliverd in the RxJavaPlugin.
     */

    @Test
    public void middleWareStreamThrows() {
        final ArrayList<Throwable> errors = initErrorHandler();
        final Middleware<Integer> middleware = upstream -> upstream.map(params -> {
            if (params.action.equals("duplicate"))
                throw new UnsupportedOperationException(params.action.toString() + " not supported");
            else
                return params.action;
        });
        final Store<Integer> store = StoreObservable.from(1, middleware, Mock.MATH_REDUCER);
        final TestObserver<Integer> observer = store.test();

        store.dispatch("increment");
        store.dispatch("duplicate");
        store.dispatch("decrement");
        store.dispatch("duplicate");

        observer.assertValues(2, 1).assertNoErrors();
        assertEquals(errors.size(), 2);
    }

    @Test
    public void reducerThrows() {
        final ArrayList<Throwable> errors = initErrorHandler();
        final Reducer<Integer> reducer = upstream -> {
            throw new RuntimeException("Reducer throws!");
        };
        final Store<Integer> store = StoreObservable.fromReducer(1, reducer);
        final TestObserver<Integer> observer = store.test();

        store.dispatch("increment");
        store.dispatch("duplicate");
        store.dispatch("decrement");

        observer.assertNoValues().assertError(RuntimeException.class);
        assertEquals(errors.size(), 0);
    }

    @Test
    public void reducerStreamThrows() {
        final ArrayList<Throwable> errors = initErrorHandler();
        final Reducer<Integer> reducer = upstream -> upstream.map(params -> {
            if (params.action.equals("increment"))
                return params.state + 1;
            else if (params.action.equals("decrement"))
                return params.state - 1;
            else
                throw new UnsupportedOperationException(params.action.toString() + " not supported");
        });
        final Store<Integer> store = StoreObservable.fromReducer(1, reducer);
        final TestObserver<Integer> observer = store.test();

        store.dispatch("increment");
        store.dispatch("duplicate");
        store.dispatch("decrement");
        store.dispatch("duplicate");

        observer.assertValues(2, 1).assertNoErrors();
        assertEquals(errors.size(), 2);
    }

    private ArrayList<Throwable> initErrorHandler() {
        final ArrayList<Throwable> errors = new ArrayList<>();

        RxJavaPlugins.setErrorHandler(errors::add);
        return errors;
    }

}
