/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.Absence_DAO;
import at.htlpinkafeld.dao.interf.Holiday_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.dao.interf.WorkTime_DAO;
import at.htlpinkafeld.mobileInterface.authorization.Secured;
import at.htlpinkafeld.mobileInterface.service.util.PATCH;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.service.TimeConverterService;
import com.google.gson.Gson;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author masix
 */
@javax.ejb.Stateless
@Path("events")
public class EventFacadeRest {

    private final User_DAO udao;
    private final WorkTime_DAO wtdao;
    private final Absence_DAO adao;
    private final Holiday_DAO hdao;

    private final Gson gson;

    public EventFacadeRest() {
        udao = DAOFactory.getDAOFactory().getUserDAO();
        wtdao = DAOFactory.getDAOFactory().getWorkTimeDAO();
        adao = DAOFactory.getDAOFactory().getAbsenceDAO();
        hdao = DAOFactory.getDAOFactory().getHolidayDAO();
        gson = new Gson();
    }

    /**
     * Returns a List of all SollZeiten, Worktimes and Holidays around the asked
     * Dates for the User
     *
     * @param username the name of the User
     * @param startDateTime ISO Local Date and Time-String for the start date
     * @param endDateTime ISO Local Date and Time-String for the end date
     * @return List of all SollZeiten, Worktimes and Holidays around the asked
     * Dates
     */
    @PATCH
    @Secured
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public String getEventList(@QueryParam("username") String username,
            @QueryParam("startDateTime") String startDateTime, @QueryParam("endDateTime") String endDateTime) {
        Map<String, String> eventList = new HashMap<>();
        Date startDate = TimeConverterService.convertLocalDateTimeToDate(LocalDateTime.parse(startDateTime));
        Date endDate = TimeConverterService.convertLocalDateTimeToDate(LocalDateTime.parse(endDateTime));

        User u = udao.getUserByUsername(username);

        eventList.put("worktime", gson.toJson(wtdao.getWorkTimesFromUserBetweenDates(u, startDate, endDate)));

        eventList.put("absence", gson.toJson(adao.getAbsencesByUser_BetweenDates(u, startDate, endDate)));
        eventList.put("holiday", gson.toJson(hdao.getHolidayBetweenDates(startDate, endDate)));

        return gson.toJson(eventList);
    }

}
