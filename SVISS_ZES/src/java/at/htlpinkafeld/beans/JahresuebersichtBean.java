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
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 *
 * @author msi
 */
public class JahresuebersichtBean {

    private final MasterBean masterBean;

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
            userL.forEach((u) -> {
                users.add(new SelectItem(u, u.getPersName()));
            });
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
        years = new ArrayList<>();
        if (selectedUser != null) {
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
                today = LocalDate.now().plusDays(1);
            } else {
                today = selectedYear.plusYears(1);
            }

            for (month = selectedYear; month.isBefore(selectedUser.getHiredate().withDayOfMonth(1)); month = month.plusMonths(1)) {
                months.add(new SelectItem(" - ", month.format(monthFormatter), "-"));
            }
            overtimeSum = 0;
            overtime19PlusSum = 0;
            for (; month.isBefore(today.withDayOfMonth(today.lengthOfMonth())) && months.size() < 12; month = month.plusMonths(1)) {

                if (today.getMonth() != month.getMonth() || today.getYear() != month.getYear()) {

                    double overtime = Math.round((calcOvertimeMinusPlus19H(selectedUser, month, month.plusMonths(1)) / 60.0) * 100.0) / 100.0;
                    double overtime19plus = Math.round((calcOvertime19Plus(selectedUser, month, month.plusMonths(1)) / 60.0) * 100.0) / 100.0;
                    months.add(new SelectItem(overtime, month.format(monthFormatter), String.valueOf(overtime19plus)));

                    overtimeSum += overtime;
                    overtime19PlusSum += overtime19plus;
                } else {
                    double overtime = Math.round((calcOvertimeMinusPlus19H(selectedUser, month, today) / 60.0) * 100.0) / 100.0;
                    double overtime19plus = Math.round((calcOvertime19Plus(selectedUser, month, today) / 60.0) * 100.0) / 100.0;
                    months.add(new SelectItem(overtime, month.format(monthFormatter), String.valueOf(overtime19plus)));

                    overtimeSum += overtime;
                    overtime19PlusSum += overtime19plus;

                }
            }

            // rounding stuff to be safe
            overtimeSum = Math.round(overtimeSum * 100.0) / 100.0;
            overtime19PlusSum = Math.round(overtime19PlusSum * 100.0) / 100.0;

            for (; month.isBefore(selectedYear.plusYears(1)); month = month.plusMonths(1)) {
                months.add(new SelectItem(" - ", month.format(monthFormatter), "-"));
            }
        }
    }

    private int calcOvertimeMinusPlus19H(User u, LocalDate startDate, LocalDate endDate) {
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
            SollZeit sz = SollZeitenService.getSollZeitenByUser_ValidDate(u, date.atStartOfDay());
            DayOfWeek dow = date.getDayOfWeek();

            if (sz != null && sz.getSollStartTimeMap().containsKey(dow)) {
                long diff = sz.getSollStartTime(dow).until(sz.getSollEndTime(dow), ChronoUnit.MINUTES);
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
            switch (a.getAbsenceType()) {
                case HOLIDAY:
                    if (!a.isAcknowledged()) {
                        break;
                    }
                    int holidayLength = (a.getEndTime().getDayOfYear() - a.getStartTime().getDayOfYear() + 1);
                    DayOfWeek hDay = a.getStartTime().getDayOfWeek();
                    for (int i = 0; i < holidayLength; i++, hDay.plus(1)) {
                        int diff = 0;
                        SollZeit sz = SollZeitenService.getSollZeitenByUser_ValidDate(u, a.getStartTime());

                        if (sz != null && sz.getSollStartTimeMap().containsKey(hDay)) {
                            diff = (int) sz.getSollStartTime(hDay).until(sz.getSollEndTime(hDay), ChronoUnit.MINUTES);
                        }
                        if (diff > 6 * 60) {
                            diff -= 30;
                        }
                        overtime += diff;

                    }
                    break;
                case TIME_COMPENSATION:
                    //Ignore because it is the same as not working = SollZeit is already deducted
//                    if (a.isAcknowledged()) {
//                        int dayNum = a.getEndTime().getDayOfYear() - a.getStartTime().getDayOfYear() + 1;
//                        DayOfWeek sDay = a.getStartTime().getDayOfWeek();
//                        for (int i = 0; i < dayNum; i++, sDay.plus(1)) {
//                            SollZeit sz = SollZeitenService.getSollZeitenByUser_DayOfWeek_ValidDate(u, a.getStartTime().getDayOfWeek(), a.getStartTime());
//                            int diff = 0;
//                            if (sz != null && sz.getSollStartTimeMap().containsKey(sDay)) {
//                                if (i == 0 || i == (dayNum - 1)) {
//                                    if (a.getStartTime().toLocalTime().isAfter(sz.getSollStartTime()) && a.getEndTime().toLocalTime().isBefore(sz.getSollEndTime())) {
//                                        diff = (int) a.getStartTime().until(a.getEndTime(), ChronoUnit.MINUTES);
//                                    } else if (a.getEndTime().toLocalTime().isBefore(sz.getSollStartTime()) || a.getStartTime().toLocalTime().isAfter(sz.getSollEndTime())) {
//                                    } else if (a.getStartTime().toLocalTime().isAfter(sz.getSollStartTime())) {
//                                        diff = (int) a.getStartTime().toLocalTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
//                                    } else if (a.getEndTime().toLocalTime().isBefore(sz.getSollEndTime())) {
//                                        diff = (int) sz.getSollStartTime().until(a.getEndTime(), ChronoUnit.MINUTES);
//                                    } else {
//                                        diff = (int) sz.getSollStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
//                                    }
//                                } else {
//                                    diff = (int) sz.getSollStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
//                                }
//                            }
//                            if (diff > 6 * 60) {
//                                diff -= 30;
//                            }
//                            overtime -= diff;
//
//                        }
//                    }
                    break;
                case MEDICAL_LEAVE:
                    int dayNum = a.getEndTime().getDayOfYear() - a.getStartTime().getDayOfYear() + 1;
                    DayOfWeek sDay = a.getStartTime().getDayOfWeek();
                    for (int i = 0; i < dayNum; i++, sDay.plus(1)) {
                        SollZeit sz = SollZeitenService.getSollZeitenByUser_ValidDate(u, a.getStartTime());
                        int diff = 0;
                        if (sz != null && sz.getSollStartTimeMap().containsKey(sDay)) {
                            if (i == 0 || i == (dayNum - 1)) {
                                if (a.getStartTime().toLocalTime().isAfter(sz.getSollStartTime(sDay)) && a.getEndTime().toLocalTime().isBefore(sz.getSollEndTime(sDay))) {
                                    diff = (int) a.getStartTime().until(a.getEndTime(), ChronoUnit.MINUTES);
                                } else if (a.getEndTime().toLocalTime().isBefore(sz.getSollStartTime(sDay)) || a.getStartTime().toLocalTime().isAfter(sz.getSollEndTime(sDay))) {
                                } else if (a.getStartTime().toLocalTime().isAfter(sz.getSollStartTime(sDay))) {
                                    diff = (int) a.getStartTime().toLocalTime().until(sz.getSollEndTime(sDay), ChronoUnit.MINUTES);
                                } else if (a.getEndTime().toLocalTime().isBefore(sz.getSollEndTime(sDay))) {
                                    diff = (int) sz.getSollStartTime(sDay).until(a.getEndTime(), ChronoUnit.MINUTES);
                                } else {
                                    diff = (int) sz.getSollStartTime(sDay).until(sz.getSollEndTime(sDay), ChronoUnit.MINUTES);
                                }
                            } else {
                                diff = (int) sz.getSollStartTime(sDay).until(sz.getSollEndTime(sDay), ChronoUnit.MINUTES);
                            }
                        }
                        if (diff > 6 * 60) {
                            diff -= 30;
                        }
                        overtime += diff;
                    }
                    break;
                case BUSINESSRELATED_ABSENCE:
                    break;
            }
        }

        List<Holiday> holidays = HolidayService.getHolidayBetweenDates(TimeConverterService.convertLocalDateToDate(startDate), TimeConverterService.convertLocalDateToDate(endDate));
        LocalDate hDate = null;
        for (Holiday h : holidays) {
            SollZeit sz = null;
            if (hDate == null) {
                sz = SollZeitenService.getSollZeitenByUser_ValidDate(u, h.getHolidayDate().atStartOfDay());
            } else if (!hDate.equals(h.getHolidayDate())) {
                sz = SollZeitenService.getSollZeitenByUser_ValidDate(u, hDate.atStartOfDay());
            }
            hDate = h.getHolidayDate();
            if (sz != null) {
                DayOfWeek dow = hDate.getDayOfWeek();

                long diff = sz.getSollStartTime(dow).until(sz.getSollEndTime(dow), ChronoUnit.MINUTES);
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
        for (WorkTime wt : workTimes) {
            overtime += wt.getOvertimeAfter19();
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
        pdf.add(new Paragraph("\nStand: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        pdf.close();
    }

    public void postProcessXLS(Object document) {
        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);

        sheet.shiftRows(0, sheet.getLastRowNum(), 2);

        HSSFRow topRow = sheet.createRow(0);

        topRow.createCell(0).setCellValue("Jahresübersicht - " + selectedYear.getYear());
        topRow.createCell(3).setCellValue("von " + selectedUser.getPersName());
        sheet.createRow(1).createCell(0).setCellValue(" ");

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

        HSSFRow header = sheet.getRow(2);
        HSSFRow footer = sheet.getRow(sheet.getLastRowNum());

        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
            HSSFCell cell = header.getCell(i);
            cell.setCellStyle(cellStyle);

            cell = footer.getCell(i);
            cell.setCellStyle(cellStyle);

            sheet.autoSizeColumn(i);
        }

        HSSFRow bottomRow = sheet.createRow(sheet.getLastRowNum() + 2);
        bottomRow.createCell(0).setCellValue("Stand: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

    }

}
