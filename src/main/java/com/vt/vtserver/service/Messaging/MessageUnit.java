package com.vt.vtserver.service.Messaging;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class MessageUnit implements Serializable {
    private Long id;
    private Double x;
    private Double vx;
    private Double y;
    private Double vy;
}
