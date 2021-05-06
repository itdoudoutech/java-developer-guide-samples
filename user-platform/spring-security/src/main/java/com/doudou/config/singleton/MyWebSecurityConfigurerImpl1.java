package com.doudou.config.singleton;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.logging.Logger;

@Order(1000)
@Configuration
public class MyWebSecurityConfigurerImpl1 implements MyWebSecurityConfigurer{

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        logger.info("MyWebSecurityConfigurerImpl1: order = 1000, " + httpSecurity);
        httpSecurity.csrf().disable()
                .authorizeRequests() // 设置权限
                .antMatchers("/hello").authenticated(); // 需要登录
    }
}
