package com.doudou.task;

import com.doudou.event.DataChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PublishEventTask implements InitializingBean {

    private final ApplicationEventPublisher eventPublisher;

    private final ScheduledExecutorService executor;

    public PublishEventTask(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void afterPropertiesSet() {
        log.info("PublishEventTask running...");
        executor.scheduleWithFixedDelay(() -> {
            final String source = "TimeMillis: " + System.currentTimeMillis();
            log.info("publish event start, source = " + source);
            DataChangedEvent event = new DataChangedEvent(source);
            eventPublisher.publishEvent(event);
            log.info("publish event end");
        }, 3, 5, TimeUnit.SECONDS);
    }
}
