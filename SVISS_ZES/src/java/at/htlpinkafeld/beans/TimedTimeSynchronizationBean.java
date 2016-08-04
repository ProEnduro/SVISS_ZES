/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;

/**
 *
 * @author Martin Six
 */
@ManagedBean(eager = true)
@ApplicationScoped
public class TimedTimeSynchronizationBean {

    ScheduledExecutorService scheduler;

    public TimedTimeSynchronizationBean() {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.systemDefault();
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNext0;
        zonedNext0 = zonedNow.withHour(0).withMinute(0).withSecond(0);
        if (zonedNow.compareTo(zonedNext0) > 0) {
            zonedNext0 = zonedNext0.plusDays(1);
        }

        Duration duration = Duration.between(zonedNow, zonedNext0);
        long initalDelay = duration.getSeconds();
        try {
            //scheduler = Executors.newSingleThreadScheduledExecutor();
            //scheduler.scheduleAtFixedRate(new TimeSynchronisationTask(), initalDelay, 24 * 60 * 60, TimeUnit.SECONDS);
            //scheduledFuture = scheduler.scheduleAtFixedRate(new TimeSynchronisationTask(), 10, 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @PreDestroy
    public void destroyThreads(){
        scheduler.shutdown();
    }

}
