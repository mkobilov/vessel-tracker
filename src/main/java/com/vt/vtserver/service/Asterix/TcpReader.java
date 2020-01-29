package com.vt.vtserver.service.Asterix;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Component
public class TcpReader implements Runnable {

    private String ip;
    private int port;
    private BlockingQueue<byte[]> rawQueue;
    private TcpManager tcpManager;
    TcpReader(BlockingQueue<byte[]> rawQueue, TcpManager tcpManager) {
        this.rawQueue = rawQueue;
        this.tcpManager = tcpManager;
    }

    public void setTcpSettings(){
        this.port = tcpManager.getPort();
        this.ip = tcpManager.getIp();
    }

    @Override
    public void run() {
        ParseTcpData();
    }
    
    private void ParseTcpData() {

        this.setTcpSettings();

        final int MAX_PACKET_SIZE = 65507;
        final int TIMEOUT = 30000;

        System.out.println("Starting TcpReader on " + ip + ":" + port);


        String listenerId = "TcpListener" + UUID.randomUUID().toString();
        try {
            createSocketListener(listenerId);
        } catch (Exception e) {
            log.error("Error in " + listenerId,e);
        }
    }
    
    private void createSocketListener(String listenerId) throws IOException, InterruptedException {
        final int MAX_PACKET_SIZE = 65507;
        final int TIMEOUT = 30000;
        log.info(listenerId + ": Starting TcpReader on " + ip + ":" + port);
        
        Socket clientSocket = new Socket(ip, port);

        clientSocket.setSoTimeout(TIMEOUT);
        byte[] buffer = new byte[MAX_PACKET_SIZE];
        DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
        while (true){
            int bytesRead = dataInputStream.read(buffer);
            if (bytesRead>0) {
                byte[] rawBytes = new byte[bytesRead];
                System.arraycopy(buffer,0,rawBytes,0,bytesRead);
                rawQueue.put(rawBytes);
            }
        }

    }
}
