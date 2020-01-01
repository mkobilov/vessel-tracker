package com.vt.vtserver.service.Messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
@RabbitListener
@RequiredArgsConstructor
public class Receiver {

    private final GeoUtils geoUtils;

    public void receiveMessage(MessageUnit messageUnit) throws InterruptedException {
        System.out.print("MessageReceived\n");
        MessageUnit result = doWork(messageUnit);
        System.out.println("Res = " + result.x + "\n");
    }

    private MessageUnit doWork(MessageUnit messageUnit) throws InterruptedException {
        //TimeUnit.SECONDS.sleep(1);
        geoUtils.CheckOnCollision(messageUnit);
        return messageUnit;
    }
}
