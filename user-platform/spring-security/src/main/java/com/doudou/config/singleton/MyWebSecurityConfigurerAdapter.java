package com.doudou.config.singleton;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "web.security.configurer.adapter", havingValue = "singleton", matchIfMissing = true)
public class MyWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    private List<MyWebSecurityConfigurer> webSecurityConfigurers;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        for (MyWebSecurityConfigurer configurer : webSecurityConfigurers) {
            configurer.configure(http);
        }
    }

    @Autowired
    public void setWebSecurityConfigurerList(@Value("#{@initWebSecurityConfigurers}") List<MyWebSecurityConfigurer> webSecurityConfigurers) {
        webSecurityConfigurers.sort(AnnotationAwareOrderComparator.INSTANCE);
        this.webSecurityConfigurers = webSecurityConfigurers;
    }

    @Bean
    public List<MyWebSecurityConfigurer> initWebSecurityConfigurers(ConfigurableListableBeanFactory beanFactory) {
        List<MyWebSecurityConfigurer> webSecurityConfigurers = new ArrayList<>();
        Map<String, MyWebSecurityConfigurer> beansOfType = beanFactory
                .getBeansOfType(MyWebSecurityConfigurer.class);
        for (Map.Entry<String, MyWebSecurityConfigurer> entry : beansOfType.entrySet()) {
            webSecurityConfigurers.add(entry.getValue());
        }
        return webSecurityConfigurers;
    }
}
