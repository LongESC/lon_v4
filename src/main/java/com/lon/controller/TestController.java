package com.lon.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;


/**
 * @author 28398
 */
@RequestMapping("/test")
@RequiredArgsConstructor
@RestController
public class TestController {

    @GetMapping(value = "/hai")
    public String HelloWorld(){
        return "Hello World";
    }

    @RequestMapping("/date")
    protected LocalTime date(){
        return LocalTime.now();
    }


}
