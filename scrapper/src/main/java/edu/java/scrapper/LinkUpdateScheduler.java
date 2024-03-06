package edu.java.scrapper;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@ConditionalOnProperty(value = "app.scheduler.enable", havingValue = "true")
public class LinkUpdateScheduler {

    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    public void update() {
        log.info("A message from scheduled method.");
    }
}
