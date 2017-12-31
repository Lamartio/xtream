package io.lamart.xtream;

import io.reactivex.Observer;
import io.reactivex.functions.Consumer;

public final class Util {

    private Util() {
        throw new Error();
    }

    public static Consumer<Object> newDispatch(final Observer<Object> observer) {
        return new Consumer<Object>() {
            @Override
            public void accept(Object action) throws Exception {
                observer.onNext(action);
            }
        };
    }

}
