package com.vt.vtserver.service;

import com.vt.vtserver.model.Alarm;
import com.vt.vtserver.repository.AlarmRepository;
import com.vt.vtserver.web.rest.dto.AlarmDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AlarmService {
    private final AlarmRepository alarmRepository;

    public Alarm postAlarm(AlarmDTO dto) {
        try {
            Alarm alarm = new Alarm();

            alarm.setRmin(dto.getRmin());
            alarm.setCollisionTime(dto.getCollisionTime());
            alarm.setCollisionObjectId(dto.getCollisionObjectId());
            alarm.setVesselTrackNumber(dto.getVesselTrackNumber());

            alarm.setCreationTime(OffsetDateTime.now(ZoneOffset.UTC));

            alarmRepository.save(alarm);

            return alarm;
        } catch (Exception e) {
            log.error("Error posting new alarm" + dto, e);
            return null;
        }
    }

    public void postAlarms(List<AlarmDTO> alarmDTOS) {
        List<Alarm> alarms = alarmDTOS.stream()
                .map(dto -> new Alarm(dto))
                .collect(Collectors.toList());

        alarmRepository.saveAll(alarms);
    }

    public void deletePreviousAlarm(Long vesselTrackNumber, Long stationaryObjectId) {

        alarmRepository.deleteByVesselTrackNumberAndCollisionObjectId(vesselTrackNumber, stationaryObjectId);
    }


}
