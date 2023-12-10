package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.StatsDtoRequest;
import ru.practicum.StatsDtoResponse;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.App;
import ru.practicum.model.Hit;
import ru.practicum.repository.MemoryApp;
import ru.practicum.repository.MemoryHit;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StatsServiceImpl implements StatsService {
    private final MemoryApp memoryApp;
    private final MemoryHit memoryHit;

    public void createHit(StatsDtoRequest request) {
        Optional<App> savedApp = memoryApp.findByName(request.getApp());
        App app = savedApp.orElseGet(() -> memoryApp.save(App.builder()
                .name(request.getApp())
                .build())
        );
        Hit hit = memoryHit.save(StatsMapper.toHit(request, app));
        log.info("New Hit saved. ID is: {}", hit.getId());
    }

    public List<StatsDtoResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (uris == null) {
            uris = Collections.emptyList();
        }
        if (unique) {
            log.info("Request for statistic with uniq accepted");
            return memoryHit.getStatsUniq(start, end, uris);
        }
        log.info("Request for statistic accepted");
        return memoryHit.getStats(start, end, uris);
    }
}