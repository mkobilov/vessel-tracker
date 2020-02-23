package com.vt.vtserver.service;

import com.vt.vtserver.model.Alarm;
import com.vt.vtserver.repository.AlarmRepository;
import com.vt.vtserver.web.rest.dto.AlarmDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AlarmService {
    private final AlarmRepository alarmRepository;

    public void postAlarms(List<AlarmDto> alarmDTOS) {
        List<Alarm> alarms = alarmDTOS.stream()
                .map(dto -> new Alarm(dto))
                .collect(Collectors.toList());

        alarmRepository.saveAll(alarms);
    }

//    public void deletePreviousAlarm(Long vesselTrackNumber, Long stationaryObjectId) {
//
//        alarmRepository.deleteByVesselTrackNumberAndCollisionObjectId(vesselTrackNumber, stationaryObjectId);
//    }
}
