package com.vt.vtserver.service.Asterix;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;

@Component
@Slf4j
@Data
public class TcpManager {
    private String ip;
    private int port;
    private BlockingQueue<byte[]> rawQueue;

    public TcpManager(BlockingQueue<byte[]> rawQueue) {
        this.rawQueue = rawQueue;
    }
}