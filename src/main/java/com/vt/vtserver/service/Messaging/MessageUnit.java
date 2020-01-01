package com.vt.vtserver.service.Messaging;

import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public class MessageUnit implements Serializable {

    Long id;
    Double x;
    Double vx;
    Double y;
    Double vy;

}
