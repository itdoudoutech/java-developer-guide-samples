package com.doudou.config.singleton;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Order(200)
@Configuration
public class MyWebSecurityConfigurerImpl2 implements MyWebSecurityConfigurer {

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        System.out.println("MyWebSecurityConfigurerImpl2: order = 200, " + httpSecurity);
        httpSecurity.csrf().disable()
                .authorizeRequests() // 设置权限
                .antMatchers("/hello").permitAll(); // 放行
    }
}
