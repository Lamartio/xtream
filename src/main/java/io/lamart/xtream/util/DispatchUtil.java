package io.lamart.xtream.util;

import io.reactivex.Emitter;
import io.reactivex.Observer;
import io.reactivex.functions.Consumer;

public final class DispatchUtil {

    private DispatchUtil() {
        throw new Error();
    }

    public static Consumer<Object> from(final Observer<Object> observer) {
        return new Consumer<Object>() {
            @Override
            public void accept(Object action) throws Exception {
                observer.onNext(action);
            }
        };
    }

    public static Consumer<Object> from(final Emitter<Object> emitter) {
        return new Consumer<Object>() {
            @Override
            public void accept(Object action) throws Exception {
                emitter.onNext(action);
            }
        };
    }

    public static Consumer<Object> empty() {
        return new Consumer<Object>() {
            @Override
            public void accept(Object action) throws Exception {

            }
        };
    }

}
