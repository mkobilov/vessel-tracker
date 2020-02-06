package com.vt.vtserver.service;

import com.vt.vtserver.model.Alarm;
import com.vt.vtserver.repository.AlarmRepository;
import com.vt.vtserver.web.rest.dto.AlarmDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;

    public Alarm postAlarm(AlarmDTO dto) {
        try {
            Alarm alarm = new Alarm();

            alarm.setRmin(dto.getRmin());
            alarm.setCollisionTime(dto.getCollisionTime());
            alarm.setCollisionObjectId(dto.getCollisionObjectId());
            alarm.setVesselTrackNumber(dto.getVesselTrackNumber());

            alarmRepository.save(alarm);

            return alarm;
        } catch (Exception e) {
            log.error("Error posting new alarm" + dto, e);
            return null;
        }
    }

    public void deletePreviousAlarm(AlarmDTO dto) {

        List<Alarm> list = alarmRepository.deleteByVesselTrackNumberAndCollisionObjectId(dto.getVesselTrackNumber(),
                dto.getCollisionObjectId());
    }
}
