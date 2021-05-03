package com.doudou.config.singleton;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface MyWebSecurityConfigurer {

    void configure(HttpSecurity httpSecurity) throws Exception;

}
