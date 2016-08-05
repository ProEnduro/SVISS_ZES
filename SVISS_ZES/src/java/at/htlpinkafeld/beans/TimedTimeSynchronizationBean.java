/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.service.HolidaySynchronisationTask;
import at.htlpinkafeld.service.OvertimeSynchronisationTask;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
        long initalDelayHoliday = duration.getSeconds();
        zonedNext0.with(DayOfWeek.MONDAY);
        duration = Duration.between(zonedNow, zonedNext0);
        long initalDelayOvertime = duration.getSeconds();
        try {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(new HolidaySynchronisationTask(), initalDelayHoliday, 24 * 60 * 60, TimeUnit.SECONDS);
            scheduler.scheduleAtFixedRate(new OvertimeSynchronisationTask(), initalDelayOvertime, 7 * 24 * 60 * 60, TimeUnit.SECONDS);
            //scheduler.scheduleAtFixedRate(new HolidaySynchronisationTask(), 10, 10, TimeUnit.SECONDS);
            // scheduler.scheduleAtFixedRate(new OvertimeSynchronisationTask(), 10, 30, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void destroyThreads() {
        scheduler.shutdown();
        if (scheduler.isShutdown()) {
            System.out.println("Threads shutdown");
        }
    }

}
