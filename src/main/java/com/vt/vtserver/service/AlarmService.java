package com.vt.vtserver.service;

import com.vt.vtserver.model.Alarm;
import com.vt.vtserver.model.Target;
import com.vt.vtserver.repository.AlarmRepository;
import com.vt.vtserver.web.rest.dto.AlarmDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;
    public Alarm postAlarm(AlarmDTO dto) {
        try {
            log.debug("postVessel start");
            Alarm alarm = new Alarm();

            alarm.setRmin(dto.getRmin());
            alarm.setTmin(dto.getTmin());
            alarm.setCollision_object_id(dto.getCollision_object_id());
            alarm.setId(dto.getVessel_id());

            return alarm;
        }catch (Exception e){
            log.error("Error posting new alarm" + dto, e);
            return null;
        }
    }
}
