package com.doudou.event;

import org.springframework.context.ApplicationEvent;

public class DataChangedEvent extends ApplicationEvent {

    public DataChangedEvent(final String source) {
        super(source);
    }
}
