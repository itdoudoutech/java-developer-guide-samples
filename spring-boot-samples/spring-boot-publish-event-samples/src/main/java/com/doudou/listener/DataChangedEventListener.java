package com.doudou.listener;

import com.doudou.event.DataChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

@Slf4j
public class DataChangedEventListener implements ApplicationListener<DataChangedEvent> {

    public DataChangedEventListener() {
        log.info("DataChangedEventListener...");
    }

    @Override
    public void onApplicationEvent(DataChangedEvent event) {
        log.info("There is an event in, source = {}", event.getSource());
    }
}
