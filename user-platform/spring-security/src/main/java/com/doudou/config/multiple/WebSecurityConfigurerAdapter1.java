package com.doudou.config.multiple;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.logging.Logger;

@Order(1000)
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "web.security.configurer.adapter", havingValue = "multiple")
public class WebSecurityConfigurerAdapter1 extends WebSecurityConfigurerAdapter {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        logger.info("WebSecurityConfigurerAdapter1 order = 1000");
        http.csrf().disable()
                .authorizeRequests() // 设置权限
                .antMatchers("/hello").authenticated(); // 需要登录
    }
}
