package com.doudou.config;

import com.doudou.listener.DataChangedEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataBaseConfiguration {

    @Bean
    public DataChangedEventListener createDataChangedEventListener() {
        return new DataChangedEventListener();
    }

}
