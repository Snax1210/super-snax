package org.snax.supersnax.controller;

import org.snax.supersnax.service.TimerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author maoth
 * @date 2022/1/5 22:19
 * @description
 */
@RestController
public class TimerController {
    @Resource
    TimerService timerService;

    @GetMapping("/timer")
    public String timer() throws InterruptedException {
        timerService.getNextSecondData();
        return "ok";
    }
}
