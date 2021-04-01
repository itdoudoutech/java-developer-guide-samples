package com.doudou.reactive.streams;

import org.junit.Test;
import org.reactivestreams.Subscriber;

public class DefaultPublisherTest {

    private final DefaultPublisher<Integer> publisher = new DefaultPublisher<>();
    private final DefaultSubscription subscription = new DefaultSubscription();
    private final Subscriber<Integer> subscriber = new DefaultSubscriber<>();

    private final long maxRequestCount = 3L;

    @Test
    public void testPublisherWithCancelCondition() {
        subscription.request(maxRequestCount);
        subscriber.onSubscribe(subscription);
        publisher.subscribe(subscriber);

        for (int i = 0; i < 4; i++) {
            if (i == 2) {
                subscription.cancel();
            }
            publisher.publish(i);
        }
    }

    @Test
    public void testPublisherWithCompleteCondition() {
        subscription.request(maxRequestCount);
        subscriber.onSubscribe(subscription);
        publisher.subscribe(subscriber);

        for (int i = 0; i < 5; i++) {
            publisher.publish(i);
        }
    }

    @Test
    public void testPublisherWithRequestAgainCondition() {
        subscription.request(maxRequestCount);
        subscriber.onSubscribe(subscription);
        publisher.subscribe(subscriber);

        for (int i = 0; i < 6; i++) {
            if (i == 4) {
                subscription.request(100L);
            }
            publisher.publish(i);
        }
    }
}
