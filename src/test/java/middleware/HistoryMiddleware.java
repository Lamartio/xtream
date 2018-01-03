package middleware;

import io.lamart.xtream.middleware.Middleware;
import io.lamart.xtream.middleware.MiddlewareParams;
import io.lamart.xtream.middleware.MiddlewareUtil;
import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BiConsumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryMiddleware<T> implements Middleware<T> {

    private final int maxSize;
    private final List<T> history = new ArrayList<T>();

    public HistoryMiddleware(T initialState) {
        this(initialState, 10);
    }

    public HistoryMiddleware(T initialState, int maxSize) {
        this.maxSize = maxSize;
        history.add(initialState);
    }

    @Override
    public ObservableSource<Object> apply(Observable<MiddlewareParams<T>> upstream) {
        return upstream.compose(MiddlewareUtil.emit(new BiConsumer<MiddlewareParams<T>, Emitter<Object>>() {
            @Override
            public void accept(MiddlewareParams<T> params, Emitter<Object> emitter) throws Exception {
                emitter.onNext(params.action);

                history.add(0, params.getState());

                while (history.size() > maxSize)
                    history.remove(maxSize);

                emitter.onNext(newHistoryAction());
            }
        }));
    }

    private HistoryChangedAction newHistoryAction() {
        final ArrayList<T> list = new ArrayList<>(history);
        final List<T> history = Collections.unmodifiableList(list);
        final HistoryChangedAction action = new HistoryChangedAction(history);

        return action;
    }

    public final class HistoryChangedAction {

        private final List<T> history;

        private HistoryChangedAction(List<T> history) {
            this.history = history;
        }

    }

}
