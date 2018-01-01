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
