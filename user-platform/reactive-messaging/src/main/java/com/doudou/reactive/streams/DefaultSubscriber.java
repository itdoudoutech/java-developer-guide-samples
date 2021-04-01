package com.doudou.reactive.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.logging.Logger;

/**
 * 订阅者 DefaultSubscriber
 *
 * @param <T>
 */
public class DefaultSubscriber<T> implements Subscriber<T> {

    protected Logger logger = Logger.getLogger(this.getClass().getName());

    private DefaultSubscription subscription;

    private boolean completed = false;

    private long requestCount = 0;

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = (DefaultSubscription) s;
    }

    @Override
    public void onNext(T t) {

        assertRequest();

        long maxRequestCount = subscription.getMaxRequest();
        if (requestCount == maxRequestCount && maxRequestCount < Long.MAX_VALUE) {
            onComplete();
            logger.warning(String.format("The number of requests is up to the max threshold[%d]," +
                    " the data subscription is completed!", maxRequestCount));
            return;
        }

        requestCount++;
        logger.info("收到数据：" + t);
    }


    @Override
    public void onError(Throwable t) {
        logger.info("遇到异常：" + t);
    }

    @Override
    public void onComplete() {
        completed = true;
        logger.info("收到数据完成");
    }

    public boolean isCanceled() {
        return subscription.isCancel();
    }

    public boolean isCompleted() {
        return completed;
    }

    private void assertRequest() {
        if (this.subscription.getMaxRequest() < 1) {
            throw new IllegalStateException("the number of request must be initialized before " +
                    "Subscriber#onNext(Object) method, please set the positive number to " +
                    "Subscription#request(int) method on Publisher#subscribe(Subscriber) phase.");
        }
    }
}
