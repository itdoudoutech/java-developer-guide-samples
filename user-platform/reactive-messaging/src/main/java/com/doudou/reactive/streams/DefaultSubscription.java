package com.doudou.reactive.streams;

import org.reactivestreams.Subscription;

/**
 * DefaultSubscription
 */
public class DefaultSubscription implements Subscription {

    private long maxRequest = -1;

    private boolean cancel = false;

    @Override
    public void request(long n) {
        if (n < 1) {
            throw new IllegalArgumentException("The number of elements to requests must be more than zero!");
        }
        this.maxRequest = n;
    }

    @Override
    public void cancel() {
        this.cancel = true;
    }

    public long getMaxRequest() {
        return maxRequest;
    }

    public boolean isCancel() {
        return cancel;
    }
}
