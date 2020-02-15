package com.vt.vtserver.service.Messaging;

import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.concurrent.TimeUnit;

@RabbitListener
@RequiredArgsConstructor
public class Receiver {
    private final GeoUtils geoUtils;

    public void receiveMessage(MessageUnit messageUnit) throws InterruptedException {
        MessageUnit result = doWork(messageUnit);
    }


    private MessageUnit doWork(MessageUnit messageUnit) throws InterruptedException {
        //TimeUnit.SECONDS.sleep(1);

        geoUtils.CheckOnCollision(messageUnit);
        return messageUnit;
    }
}
