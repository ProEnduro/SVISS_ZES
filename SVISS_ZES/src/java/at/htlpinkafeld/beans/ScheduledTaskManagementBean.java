/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.service.AbsenceCleaningTask;
import at.htlpinkafeld.service.HolidaySynchronisationTask;
import at.htlpinkafeld.service.OvertimeSynchronisationTask;
import at.htlpinkafeld.service.ParttimerWeektimeDeductionTask;
import at.htlpinkafeld.service.UpdateUserHistoryTask;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Martin Six
 */
//@ManagedBean(eager = true)
//@ApplicationScoped
public class ScheduledTaskManagementBean implements ServletContextListener {

    ScheduledExecutorService scheduler;

//    public ScheduledTaskManagementBean() {
//        LocalDateTime localNow = LocalDateTime.now();
//        ZoneId currentZone = ZoneId.systemDefault();
//        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
//        ZonedDateTime zonedNext0;
//        zonedNext0 = zonedNow.withHour(3).withMinute(0).withSecond(0);
//
//        if (zonedNow.compareTo(zonedNext0) > 0) {
//            zonedNext0 = zonedNext0.plusDays(1);
//        }
//
//        Duration duration = Duration.between(zonedNow, zonedNext0);
//        long initalDelayOvertime = duration.getSeconds();
//        zonedNext0 = zonedNext0.withHour(10);
//        duration = Duration.between(zonedNow, zonedNext0);
//        long initalDelayEmail = duration.getSeconds();
//        try {
//            scheduler = Executors.newSingleThreadScheduledExecutor();
//            scheduler.scheduleAtFixedRate(new HolidaySynchronisationTask(), initalDelayOvertime, 24 * 60 * 60, TimeUnit.SECONDS);
//            scheduler.scheduleAtFixedRate(new OvertimeSynchronisationTask(), initalDelayOvertime, 24 * 60 * 60, TimeUnit.SECONDS);
//            scheduler.scheduleAtFixedRate(new AbsenceCleaningTask(), initalDelayEmail, 24 * 60 * 60, TimeUnit.SECONDS);
//            scheduler.scheduleAtFixedRate(new UpdateUserHistoryTask(), initalDelayOvertime, 24 * 60 * 60, TimeUnit.SECONDS);
//        } catch (Exception e) {
//            Logger.getLogger(ScheduledTaskManagementBean.class.getName()).log(Level.SEVERE, null, e);
//
//        }
//    }
//
//    @PreDestroy
//    public void destroyThreads() {
//        scheduler.shutdown();
//    }
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.systemDefault();
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNext0;
        zonedNext0 = zonedNow.withHour(3).withMinute(0).withSecond(0);

        if (zonedNow.compareTo(zonedNext0) > 0) {
            zonedNext0 = zonedNext0.plusDays(1);
        }

        Duration duration = Duration.between(zonedNow, zonedNext0);
        long initalDelayOvertime = duration.getSeconds();
        zonedNext0 = zonedNext0.withHour(10);
        duration = Duration.between(zonedNow, zonedNext0);
        long initalDelayEmail = duration.getSeconds();

        ZonedDateTime zonedSunday = ZonedDateTime.of(localNow.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)), currentZone);
        zonedSunday = zonedSunday.withHour(22);
        Duration timeToNextSunday = Duration.between(zonedNow, zonedSunday);
        long timeToNextSundayLong = timeToNextSunday.getSeconds();

        try {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(new HolidaySynchronisationTask(), initalDelayOvertime, 24 * 60 * 60, TimeUnit.SECONDS);
            scheduler.scheduleAtFixedRate(new OvertimeSynchronisationTask(), initalDelayOvertime, 24 * 60 * 60, TimeUnit.SECONDS);
            scheduler.scheduleAtFixedRate(new AbsenceCleaningTask(), initalDelayEmail, 24 * 60 * 60, TimeUnit.SECONDS);
            scheduler.scheduleAtFixedRate(new UpdateUserHistoryTask(), initalDelayOvertime, 24 * 60 * 60, TimeUnit.SECONDS);

            //Parttimer Weektime Deduction
            //RequestContext.getCurrentInstance().execute("PrimeFaces.info('TimeToNextSunday: " + timeToNextSundayLong +"');");
            Logger.getLogger(ScheduledTaskManagementBean.class.getName()).log(Level.INFO, "TimeToNextSunday: " + timeToNextSundayLong);
            scheduler.scheduleAtFixedRate(new ParttimerWeektimeDeductionTask(), timeToNextSundayLong, 7 * 24 * 60 * 60, TimeUnit.SECONDS);
        } catch (Exception e) {
            Logger.getLogger(ScheduledTaskManagementBean.class.getName()).log(Level.SEVERE, null, e);

        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        scheduler.shutdown();
    }

}
