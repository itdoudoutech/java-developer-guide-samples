package com.doudou.config.singleton;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.logging.Logger;

@Order(200)
@Configuration
public class MyWebSecurityConfigurerImpl2 implements MyWebSecurityConfigurer {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        logger.info("MyWebSecurityConfigurerImpl2: order = 200, " + httpSecurity);
        httpSecurity.csrf().disable()
                .authorizeRequests() // 设置权限
                .antMatchers("/hello").permitAll(); // 放行
    }
}
