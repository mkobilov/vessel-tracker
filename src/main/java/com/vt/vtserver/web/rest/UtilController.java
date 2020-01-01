package com.vt.vtserver.web.rest;

import com.vt.vtserver.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/util")
public class UtilController {

    private final RabbitTemplate rabbitTemplate;

    private final ApplicationProperties applicationProperties;

    @GetMapping("/frequency")
    public Long getFrequency(){
        return applicationProperties.getFrequency();
    }

    @PostMapping("/message")
    public void sendMessage(@RequestParam Long value){
        int i = 0;

        for(i = 0; i < 9; i++){
            System.out.println("Sending message..." + i);
            rabbitTemplate.convertAndSend(applicationProperties.getQueue(), (value+i));
        }
    }
}
