/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.Holiday;
import at.htlpinkafeld.pojo.SollZeit;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.WorkTime;
import at.htlpinkafeld.service.AbsenceService;
import at.htlpinkafeld.service.AccessRightsService;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import at.htlpinkafeld.service.HolidayService;
import at.htlpinkafeld.service.IstZeitService;
import at.htlpinkafeld.service.SollZeitenService;
import at.htlpinkafeld.service.TimeConverterService;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.draw.VerticalPositionMark;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

/**
 *
 * @author msi
 */
public class JahresuebersichtBean {

    private MasterBean masterBean;

    private List<SelectItem> users;
    private User selectedUser;

    private List<SelectItem> years;
    private LocalDate selectedYear;

    private List<SelectItem> months;

    private double overtimeSum;
    private double overtime19PlusSum;

    private final DateTimeFormatter monthFormatter;

    /**
     * Creates a new instance of alleUserBean
     */
    public JahresuebersichtBean() {
        monthFormatter = DateTimeFormatter.ofPattern("MMMM");

        FacesContext context = FacesContext.getCurrentInstance();
        masterBean = (MasterBean) context.getApplication().evaluateExpressionGet(context, "#{masterBean}", MasterBean.class);

    }

    public void loadJahresübersichtBean() {

        User currentUser = masterBean.getUser();
        users = new ArrayList<>();
        if (AccessRightsService.checkPermission(currentUser.getAccessLevel(), "EVALUATE_ALL")) {
            List<User> userL = BenutzerverwaltungService.getUserByDisabled(Boolean.FALSE);
            for (User u : userL) {
                users.add(new SelectItem(u, u.getPersName()));
            }
        } else if (AccessRightsService.checkPermission(currentUser.getAccessLevel(), "EVALUATE_SELF")) {
            users.add(new SelectItem(currentUser, currentUser.getPersName()));
        }

    }

    public List<SelectItem> getUsers() {
        return users;
    }

    public void setUsers(List<SelectItem> users) {
        this.users = users;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public List<SelectItem> getYears() {
        return years;
    }

    public void setYears(List<SelectItem> years) {
        this.years = years;
    }

    public LocalDate getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(LocalDate selectedYear) {
        this.selectedYear = selectedYear;
    }

    public List<SelectItem> getMonths() {
        return months;
    }

    public void setMonths(List<SelectItem> months) {
        this.months = months;
    }

    public double getOvertimeSum() {
        return overtimeSum;
    }

    public double getOvertime19PlusSum() {
        return overtime19PlusSum;
    }

    public void loadYears() {
        if (selectedUser != null) {
            years = new ArrayList<>();
            LocalDate hireyear = selectedUser.getHiredate().withDayOfYear(1);
            for (LocalDate year = hireyear; year.isBefore(LocalDate.now().withDayOfYear(1)) || year.isEqual(LocalDate.now().withDayOfYear(1)); year = year.plusYears(1)) {
                years.add(new SelectItem(year, String.valueOf(year.getYear())));
            }
        }
    }

    public void loadData() {
        if (selectedYear != null && selectedUser != null) {
            months = new ArrayList<>();

            LocalDate month;
            LocalDate today;
            if (selectedYear.getYear() == LocalDate.now().getYear()) {
                today = LocalDate.now();
            } else {
                today = selectedYear.plusYears(1).minusDays(1);
            }

            for (month = selectedYear; month.isBefore(selectedUser.getHiredate().withDayOfMonth(1)); month = month.plusMonths(1)) {
                months.add(new SelectItem(" - ", month.format(monthFormatter), "-"));
            }
            overtimeSum = 0;
            overtime19PlusSum = 0;
            DecimalFormat df = new DecimalFormat("#.##");
            for (; month.isBefore(today.withDayOfMonth(today.lengthOfMonth())); month = month.plusMonths(1)) {
                if (today.getMonth() != month.getMonth()) {

                    double overtime = Double.parseDouble(df.format(calcOvertimeMinusPlus19H(selectedUser, month, month.plusMonths(1)) / 60.0));
                    double overtime19plus = Double.parseDouble(df.format(calcOvertime19Plus(selectedUser, month, month.plusMonths(1)) / 60.0));
                    months.add(new SelectItem(overtime, month.format(monthFormatter), String.valueOf(overtime19plus)));

                    overtimeSum += overtime;
                    overtime19PlusSum += overtime19plus;
                } else {
                    months.add(new SelectItem(Double.parseDouble(df.format(calcOvertimeMinusPlus19H(selectedUser, month, today) / 60.0)),
                            month.format(monthFormatter), String.valueOf(Double.parseDouble(df.format(calcOvertime19Plus(selectedUser, month, today) / 60.0)))));
                }
            }
            for (; month.isBefore(selectedYear.plusYears(1)); month = month.plusMonths(1)) {
                months.add(new SelectItem(" - ", month.format(monthFormatter), "-"));
            }
        }
    }

    public int calcOvertimeMinusPlus19H(User u, LocalDate startDate, LocalDate endDate) {
        int overtime = 0;

        List<WorkTime> workTimes = IstZeitService.getWorkTimeForUserBetweenStartAndEndDate(u, TimeConverterService.convertLocalDateToDate(startDate), TimeConverterService.convertLocalDateToDate(endDate));

        LocalTime lt19Plus = LocalTime.of(19, 0);

        for (WorkTime wt : workTimes) {
            if (lt19Plus.isBefore(wt.getEndTime().toLocalTime())) {
                overtime += wt.getStartTime().until(wt.getEndTime().with(lt19Plus), ChronoUnit.MINUTES);
            } else {
                overtime += wt.getStartTime().until(wt.getEndTime(), ChronoUnit.MINUTES);
            }
            overtime -= wt.getBreakTime();
        }

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            SollZeit sz = SollZeitenService.getSollZeitenByUser_DayOfWeek_ValidDate(u, date.getDayOfWeek(), date.atStartOfDay());
            if (sz != null) {
                long diff = sz.getSollStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
                if (diff >= 6 * 60) {
                    diff -= 30;
                }
                overtime -= diff;
            }
        }

//        overtime -= u.getWeekTime() * 60;
        List<Absence> absences = AbsenceService.getAbsencesByUserBetweenDates(u, TimeConverterService.convertLocalDateToDate(startDate), TimeConverterService.convertLocalDateToDate(endDate));

        for (Absence a : absences) {
            if (a.getStartTime().isBefore(startDate.atStartOfDay())) {
                a.setStartTime(startDate.atStartOfDay());
            }
            if (a.getEndTime().isAfter(endDate.atTime(23, 59, 59))) {
                a.setEndTime(endDate.atStartOfDay().minusSeconds(1));
            }
            switch (a.getAbsenceType().getAbsenceName()) {
                case "holiday":
                    if (!a.isAcknowledged()) {
                        break;
                    }
                    int holidayLength = (a.getEndTime().getDayOfYear() - a.getStartTime().getDayOfYear() + 1);
                    DayOfWeek hDay = a.getStartTime().getDayOfWeek();
                    for (int i = 0; i < holidayLength; i++, hDay.plus(1)) {
                        int diff = 0;
                        SollZeit sz = SollZeitenService.getSollZeitenByUser_DayOfWeek_ValidDate(u, a.getStartTime().getDayOfWeek(), a.getStartTime());
                        if (sz != null) {
                            diff = (int) sz.getSollStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
                        }
                        if (diff > 6 * 60) {
                            diff -= 30;
                        }
                        overtime += diff;

                    }
                    break;
                case "time compensation":
                    if (a.isAcknowledged()) {
                        int dayNum = a.getEndTime().getDayOfYear() - a.getStartTime().getDayOfYear() + 1;
                        DayOfWeek sDay = a.getStartTime().getDayOfWeek();
                        for (int i = 0; i < dayNum; i++, sDay.plus(1)) {
                            SollZeit sz = SollZeitenService.getSollZeitenByUser_DayOfWeek_ValidDate(u, a.getStartTime().getDayOfWeek(), a.getStartTime());
                            int diff = 0;
                            if (sz != null) {
                                if (i == 0 || i == (dayNum - 1)) {
                                    if (a.getStartTime().toLocalTime().isAfter(sz.getSollStartTime()) && a.getEndTime().toLocalTime().isBefore(sz.getSollEndTime())) {
                                        diff = (int) a.getStartTime().until(a.getEndTime(), ChronoUnit.MINUTES);
                                    } else if (a.getEndTime().toLocalTime().isBefore(sz.getSollStartTime()) || a.getStartTime().toLocalTime().isAfter(sz.getSollEndTime())) {
                                    } else if (a.getStartTime().toLocalTime().isAfter(sz.getSollStartTime())) {
                                        diff = (int) a.getStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
                                    } else if (a.getEndTime().toLocalTime().isBefore(sz.getSollEndTime())) {
                                        diff = (int) sz.getSollStartTime().until(a.getEndTime(), ChronoUnit.MINUTES);
                                    } else {
                                        diff = (int) sz.getSollStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
                                    }
                                } else {
                                    diff = (int) sz.getSollStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
                                }
                            }
                            if (diff > 6 * 60) {
                                diff -= 30;
                            }
                            overtime -= diff;

                        }
                    }
                    break;
                case "medical leave":
                    int dayNum = a.getEndTime().getDayOfYear() - a.getStartTime().getDayOfYear() + 1;
                    DayOfWeek sDay = a.getStartTime().getDayOfWeek();
                    for (int i = 0; i < dayNum; i++, sDay.plus(1)) {
                        SollZeit sz = SollZeitenService.getSollZeitenByUser_DayOfWeek_ValidDate(u, a.getStartTime().getDayOfWeek(), a.getStartTime());
                        int diff = 0;
                        if (sz != null) {
                            if (i == 0 || i == (dayNum - 1)) {
                                if (a.getStartTime().toLocalTime().isAfter(sz.getSollStartTime()) && a.getEndTime().toLocalTime().isBefore(sz.getSollEndTime())) {
                                    diff = (int) a.getStartTime().until(a.getEndTime(), ChronoUnit.MINUTES);
                                } else if (a.getEndTime().toLocalTime().isBefore(sz.getSollStartTime()) || a.getStartTime().toLocalTime().isAfter(sz.getSollEndTime())) {
                                } else if (a.getStartTime().toLocalTime().isAfter(sz.getSollStartTime())) {
                                    diff = (int) a.getStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
                                } else if (a.getEndTime().toLocalTime().isBefore(sz.getSollEndTime())) {
                                    diff = (int) sz.getSollStartTime().until(a.getEndTime(), ChronoUnit.MINUTES);
                                } else {
                                    diff = (int) sz.getSollStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
                                }
                            } else {
                                diff = (int) sz.getSollStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
                            }
                        }
                        if (diff > 6 * 60) {
                            diff -= 30;
                        }
                        overtime += diff;
                    }
                    break;
                case "business-related absence":
                    break;
            }
        }

        List<Holiday> holidays = HolidayService.getHolidayBetweenDates(TimeConverterService.convertLocalDateToDate(startDate), TimeConverterService.convertLocalDateToDate(endDate));
        LocalDate hDate = null;
        for (Holiday h : holidays) {
            SollZeit sz = null;
            if (hDate == null) {
                sz = SollZeitenService.getSollZeitenByUser_DayOfWeek_ValidDate(u, h.getHolidayDate().getDayOfWeek(), h.getHolidayDate().atStartOfDay());
            } else if (!hDate.equals(h.getHolidayDate())) {
                sz = SollZeitenService.getSollZeitenByUser_DayOfWeek_ValidDate(u, h.getHolidayDate().getDayOfWeek(), hDate.atStartOfDay());
            }
            hDate = h.getHolidayDate();
            if (sz != null) {
                long diff = sz.getSollStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
                if (diff >= 6 * 60) {
                    diff -= 30;
                }
                overtime += diff;
            }
        }

        return overtime;
    }

    public int calcOvertime19Plus(User u, LocalDate startDate, LocalDate endDate) {
        int overtime = 0;

        List<WorkTime> workTimes = IstZeitService.getWorkTimeForUserBetweenStartAndEndDate(u, TimeConverterService.convertLocalDateToDate(startDate), TimeConverterService.convertLocalDateToDate(endDate));
        LocalTime lt19Plus = LocalTime.of(19, 0);

        for (WorkTime wt : workTimes) {
            if (lt19Plus.isBefore(wt.getEndTime().toLocalTime())) {
                overtime += lt19Plus.until(wt.getEndTime().toLocalTime(), ChronoUnit.MINUTES);
            }

        }
        return overtime;
    }

    public void preProcessPDF(Object document) throws DocumentException {
        Document pdf = (Document) document;
        pdf.open();
        pdf.setPageSize(PageSize.A4);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell(getCell("Jahresübersicht - " + selectedYear.getYear(), PdfPCell.ALIGN_LEFT));
        table.addCell(getCell("", PdfPCell.ALIGN_CENTER));
        table.addCell(getCell("von " + selectedUser.getPersName(), PdfPCell.ALIGN_RIGHT));
        pdf.add(table);

        pdf.add(new Paragraph("\n"));
    }

    private PdfPCell getCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public void postProcessPDF(Object document) throws DocumentException {
        Document pdf = (Document) document;
        pdf.add(new Paragraph("\n\n\n\nStand: " + LocalDate.now()));
        pdf.close();
    }

}
