package com.doudou.config;

import com.doudou.server.WebsocketCollector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebsocketConfig {

    @Bean
    @ConditionalOnMissingBean(WebsocketCollector.class)
    public WebsocketCollector websocketCollector() {
        return new WebsocketCollector();
    }

    @Bean
    @ConditionalOnMissingBean(ServerEndpointExporter.class)
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
