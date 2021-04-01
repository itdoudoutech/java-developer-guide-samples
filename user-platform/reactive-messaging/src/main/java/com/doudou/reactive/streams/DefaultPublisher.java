package com.doudou.reactive.streams;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 发布者 DefaultPublisher
 *
 * @param <T>
 */
public class DefaultPublisher<T> implements Publisher<T> {

    protected Logger logger = Logger.getLogger(this.getClass().getName());

    private final List<DefaultSubscriber<? super T>> subscribers = new LinkedList<>();

    @Override
    public void subscribe(Subscriber<? super T> s) {
        DefaultSubscriber<? super T> defaultSubscriber = (DefaultSubscriber) s;
        subscribers.add(defaultSubscriber);
    }

    public void publish(T data) {
        subscribers.forEach(p -> {
            if (p.isCompleted()) {
                logger.severe(String.format("The data subscription was completed, current data[%s] should not be published again!", data));
                return;
            }

            if (p.isCanceled()) { // Indicates that the Subscriber invokes Subscription#cancel() method.
                logger.warning(String.format("The Subscriber has canceled the data subscription," +
                        " current data[%s] will be ignored!", data));
                return;
            }
        });

        subscribers.stream().filter(p -> !p.isCanceled() && !p.isCompleted())
                .forEach(subscriber -> subscriber.onNext(data));
    }
}
