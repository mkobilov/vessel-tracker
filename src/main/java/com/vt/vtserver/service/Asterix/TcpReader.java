package com.vt.vtserver.service.Asterix;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Component
public class TcpReader implements Runnable {
    private BlockingQueue<byte[]> rawQueue;
    private TcpManager tcpManager;

    private byte[] buffer;
    private DataInputStream dataInputStream;

    TcpReader(BlockingQueue<byte[]> rawQueue, TcpManager tcpManager) {
        this.rawQueue = rawQueue;
        this.tcpManager = tcpManager;
    }

    @Override
    public void run() {
        ParseTcpData();
    }

    private void ParseTcpData() {
        log.info("Starting TcpReader on " + tcpManager.getIp() + ":" + tcpManager.getPort());

        String listenerId = "TcpListener" + UUID.randomUUID().toString();
        try {
            createSocketListener(listenerId);
        } catch (InterruptedException e) {
            log.error("Socket creation interrupted" + listenerId, e);
        } catch (IOException e) {
            log.error("Socket creation is impossible" + listenerId, e);
        } catch (Exception e) {
            log.error("Unknown Error in " + listenerId, e);
        }
    }

    private void createSocketListener(String listenerId) throws IOException, InterruptedException {

        log.info(listenerId + ": Starting TcpReader on " + tcpManager.getIp() + ":" + tcpManager.getPort());

        Socket clientSocket = new Socket(tcpManager.getIp(), tcpManager.getPort());

        clientSocket.setSoTimeout(CommonConstants.TIMEOUT);
        buffer = new byte[CommonConstants.MAX_PACKET_SIZE];
        dataInputStream = new DataInputStream(clientSocket.getInputStream());

        while (true) {
            try {
                int bytesRead = dataInputStream.read(buffer);
                if (bytesRead > 0) {
                    byte[] rawBytes = new byte[bytesRead];
                    System.arraycopy(buffer, 0, rawBytes, 0, bytesRead);
                    rawQueue.put(rawBytes);
                }
            } catch (IOException e) {
                log.warn("Connection with radar is interrupted", e);
                retryConnection();
            }
        }

    }

    private void retryConnection() throws IOException {
        Socket clientSocket = new Socket(tcpManager.getIp(), tcpManager.getPort());

        clientSocket.setSoTimeout(CommonConstants.TIMEOUT);
        buffer = new byte[CommonConstants.MAX_PACKET_SIZE];
        dataInputStream = new DataInputStream(clientSocket.getInputStream());
    }
}
