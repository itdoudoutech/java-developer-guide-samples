package com.doudou.controller;

import com.doudou.base.ResultBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public ResultBean<String> hello1(String name) {
        return new ResultBean<>("Hello, " + (null == name ? "" : name));
    }
}
