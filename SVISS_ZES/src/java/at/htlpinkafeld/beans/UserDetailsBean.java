/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.dao.interf.AbsenceType_DAO;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.Holiday;
import at.htlpinkafeld.pojo.SollZeit;
import at.htlpinkafeld.pojo.TimeRowDisplay;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserHistoryEntry;
import at.htlpinkafeld.pojo.WorkTime;
import at.htlpinkafeld.service.AbsenceService;
import at.htlpinkafeld.service.AccessRightsService;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import at.htlpinkafeld.service.HolidayService;
import at.htlpinkafeld.service.IstZeitService;
import at.htlpinkafeld.service.SollZeitenService;
import at.htlpinkafeld.service.TimeConverterService;
import at.htlpinkafeld.service.UserHistoryService;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
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
public class UserDetailsBean {
//TODO enable view for current month
//TODO default ComboBox entries

    /**
     * Creates a new instance of UserDetailsBean
     */
    List<String> userAsStringList;
    String selectedUser;
    List<SelectItem> dates;
    LocalDate selectedDate;

    List<TimeRowDisplay> timerowlist;

    double saldo;
    double überstundenNach19;
    int urlaubsanspruch;

    int selectedYear = 0;
    List<Integer> years;

    User currentUser;

    public List<TimeRowDisplay> getTimerowlist() {
        return timerowlist;
    }

    public void setTimerowlist(List<TimeRowDisplay> timerowlist) {
        this.timerowlist = timerowlist;
    }

    public UserDetailsBean() {
        userAsStringList = new ArrayList<>();

        FacesContext context = FacesContext.getCurrentInstance();
        MasterBean masterBean = (MasterBean) context.getApplication().evaluateExpressionGet(context, "#{masterBean}", MasterBean.class);
        currentUser = masterBean.getUser();

        if (currentUser != null) {
            if (AccessRightsService.checkPermission(currentUser.getAccessLevel(), "EVALUATE_ALL")) {
                for (User u : BenutzerverwaltungService.getUserList()) {
                    userAsStringList.add(u.getUsername());
                }
            } else if (AccessRightsService.checkPermission(currentUser.getAccessLevel(), "EVALUATE_SELF")) {
                userAsStringList.add(currentUser.getUsername());
            }
        }

        dates = new ArrayList<>();

    }

    public void reloadUsers() {
        userAsStringList = new ArrayList<>();

        if (AccessRightsService.checkPermission(currentUser.getAccessLevel(), "EVALUATE_ALL")) {
            for (User u : BenutzerverwaltungService.getUserList()) {
                userAsStringList.add(u.getUsername());
            }
        } else if (AccessRightsService.checkPermission(currentUser.getAccessLevel(), "EVALUATE_SELF")) {
            userAsStringList.add(currentUser.getUsername());
        }

        dates = new ArrayList<>();

        this.setSelectedUser(userAsStringList.get(0));
        this.loadMonthOverview(null);
    }

    public List<String> getUserAsStringList() {
        return userAsStringList;
    }

    public void setUserAsStringList(List<String> userAsStringList) {
        this.userAsStringList = userAsStringList;
    }

    public String getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(String selectedUser) {
        this.selectedUser = selectedUser;

        SelectItem si;

        this.selectedDate = null;
        this.selectedYear = 0;

        dates.clear();
        years = new ArrayList<>();

        List<UserHistoryEntry> uhelist = UserHistoryService.getUserHistoryEntriesForUser(BenutzerverwaltungService.getUserByUsername(selectedUser));

        if (!uhelist.isEmpty()) {
            int firstyear = uhelist.get(0).getTimestamp().getYear();
            int lastyear = uhelist.get(uhelist.size() - 1).getTimestamp().getYear();

            List<Integer> intlist = new ArrayList<>();

            for (int i = firstyear; i <= lastyear; i++) {
                intlist.add(i);
            }

            this.setSelectedYear(firstyear);

            this.setYears(intlist);
        }
    }

    public List<SelectItem> getDates() {
        return dates;
    }

    public void setDates(List<SelectItem> dates) {
        this.dates = dates;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;

//        loadMonthOverview(null);
        if (this.selectedYear != 0) {
            for (UserHistoryEntry uhe : UserHistoryService.getUserHistoryEntriesForUserBetweenDates(BenutzerverwaltungService.getUserByUsername(selectedUser), selectedDate, selectedDate.plusDays(1))) {
                this.urlaubsanspruch = uhe.getVacation();
            }
        }

    }

    public void loadMonthOverview(ActionEvent e) {
        this.timerowlist = new ArrayList<>();

        TimeRowDisplay trd;

        saldo = 0;
        überstundenNach19 = 0;

        User currentUser = BenutzerverwaltungService.getUser(selectedUser);

        if (selectedDate != null) {
            int max = this.selectedDate.lengthOfMonth();

            Double saldotemp = 0.0;

            LocalDate temp;
            temp = selectedDate.withDayOfMonth(1);

            for (int i = 0; i < max; i++) {

                trd = new TimeRowDisplay(new Holiday(temp, "nicht erschienen!"));
                trd.setReason("");

                Date start = TimeConverterService.convertLocalDateToDate(temp);
                Date end = TimeConverterService.convertLocalDateToDate(temp.plusDays(1));

                List<WorkTime> worklist = IstZeitService.getWorkTimeForUserBetweenStartAndEndDate(currentUser, start, end);
                List<Absence> absencelist = AbsenceService.getAbsencesByUserBetweenDates(currentUser, start, end);
                List<Holiday> holidaylist = HolidayService.getHolidayBetweenDates(start, end);

                if (!worklist.isEmpty()) {
                    trd = new TimeRowDisplay(worklist.get(0));

                    Double worktime = trd.getWorkTime();
                    Double sollzeit = trd.getSollZeit();
                    Double breaktime = worklist.get(0).getBreakTime() * 1.0;

                    saldotemp = worktime - sollzeit;

                    überstundenNach19 += trd.getOverTime19plus();

                    if (worklist.size() > 1) {
                        for (WorkTime w : worklist.subList(1, worklist.size() - 1)) {
                            trd.setReason(trd.getReason() + " " + worklist.size() + ". Arbeitszeit von " + w.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " bis " + w.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " ");
                        }
                    }
                }

                if (!holidaylist.isEmpty()) {
                    trd.setReason(holidaylist.get(0).getHolidayComment() + " ");
                    saldotemp = 0.0;
                }
                if (!absencelist.isEmpty()) {
                    for (Absence a : absencelist) {
                        trd.setReason(trd.getReason() + a.getAbsenceType().getAbsenceName() + " " + a.getReason() + " ");

                        if (!worklist.isEmpty() && (!a.getAbsenceType().getAbsenceName().equals(AbsenceType_DAO.TIMECOMPENSATION) && !a.getAbsenceType().getAbsenceName().equals(AbsenceType_DAO.BUSINESSRELATED_ABSENCE))) {
                            double smax;
                            double s;
                            SollZeit sollzeit = SollZeitenService.getSollZeitenByUser_DayOfWeek_ValidDate(currentUser, temp.getDayOfWeek(), temp.atStartOfDay());

                            if (sollzeit != null) {
                                if (a.getStartTime().isBefore(temp.atStartOfDay())) {
                                    a.setStartTime(temp.atStartOfDay());
                                }
                                if (a.getEndTime().isAfter(temp.atTime(23, 59))) {
                                    a.setEndTime(temp.atTime(23, 59));
                                }
                                if (a.getEndTime().isAfter(sollzeit.getSollEndTime().atDate(temp))) {
                                    a.setEndTime(sollzeit.getSollEndTime().atDate(temp));
                                }
                                if (a.getStartTime().isBefore(sollzeit.getSollStartTime().atDate(temp))) {
                                    a.setStartTime(sollzeit.getSollStartTime().atDate(temp));
                                }

                                if (a.getStartTime().isBefore(sollzeit.getSollEndTime().atDate(temp))) {
                                    smax = a.getStartTime().toLocalTime().until(sollzeit.getSollEndTime(), ChronoUnit.MINUTES);
                                    s = a.getStartTime().until(a.getEndTime(), ChronoUnit.MINUTES);

                                    if (s > smax) {
                                        saldo += smax / 60.0;
                                    } else {
                                        saldo += s / 60.0;
                                    }
                                }

                            }
                        }
                    }
                }

                if (worklist.isEmpty() && absencelist.isEmpty() && holidaylist.isEmpty()) {
                    SollZeit sollzeit = SollZeitenService.getSollZeitenByUser_DayOfWeek_ValidDate(currentUser, temp.getDayOfWeek(), temp.atStartOfDay());

                    if (sollzeit == null) {
                        trd = new TimeRowDisplay(new Holiday(temp, "(frei)"));
                        saldotemp = 0.0;
                    }
                }
                this.timerowlist.add(trd);
                saldo += saldotemp;
                temp = temp.plus(1, ChronoUnit.DAYS);
            }
        }
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public double getÜberstundenNach19() {
        return überstundenNach19;
    }

    public void setÜberstundenNach19(double überstundenNach19) {
        this.überstundenNach19 = überstundenNach19;
    }

    public int getUrlaubsanspruch() {
        return urlaubsanspruch;
    }

    public void setUrlaubsanspruch(int urlaubsanspruch) {
        this.urlaubsanspruch = urlaubsanspruch;
    }

    public int getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(int selectedYear) {
        this.selectedYear = selectedYear;
    }

    public List<Integer> getYears() {
        return years;
    }

    public void setYears(List<Integer> years) {
        this.years = years;
        loadMonths();
    }

    public void loadMonths() {

        this.dates.clear();

        LocalDate start = LocalDate.of(selectedYear, Month.JANUARY, 1);
        LocalDate end = start.plusYears(1);

        SelectItem si;

        this.selectedDate = null;

        for (UserHistoryEntry uhe : UserHistoryService.getUserHistoryEntriesForUserBetweenDates(BenutzerverwaltungService.getUserByUsername(selectedUser), start, end)) {

            si = new SelectItem(uhe.getTimestamp().toLocalDate(), uhe.getTimestamp().toLocalDate().format(DateTimeFormatter.ofPattern("MMMM")));

            if (this.selectedDate == null) {
                this.setSelectedDate((LocalDate) si.getValue());
                urlaubsanspruch = uhe.getVacation();
            }

            this.dates.add(si);
        }

    }

    public void preProcessPDF(Object document) throws DocumentException {
        Document pdf = (Document) document;
        pdf.setPageSize(PageSize.A4.rotate());
        pdf.open();

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell(getCell("Monatsübersicht - " + selectedDate.format(DateTimeFormatter.ofPattern("MM.yyyy")), PdfPCell.ALIGN_LEFT));
        table.addCell(getCell("", PdfPCell.ALIGN_CENTER));
        table.addCell(getCell("von " + selectedUser, PdfPCell.ALIGN_RIGHT));
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

        topRow.createCell(0).setCellValue("Monatsübersicht - " + selectedDate.format(DateTimeFormatter.ofPattern("MM.yyyy")));
        topRow.createCell(7).setCellValue("von " + selectedUser);
        sheet.createRow(1).createCell(0).setCellValue(" ");

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

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

    public void createPDF() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);

            if (!document.isOpen()) {
                document.open();
            }

            preProcessPDF(document);

            PdfPTable pdfTable = exportPDFTable();
            document.add(pdfTable);

            postProcessPDF(document);

            //Keep modifying your pdf file (add pages and more)
            document.close();
            String fileName = "Monatszeiten";

            writePDFToResponse(context.getExternalContext(), baos, fileName);

            context.responseComplete();
        } catch (DocumentException ex) {
            Logger.getLogger(UserDetailsBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private PdfPTable exportPDFTable() {
        int numberOfColumns = 8;
        PdfPTable pdfTable = new PdfPTable(numberOfColumns);
        pdfTable.setWidthPercentage(100);

        float[] widths = {7, 7, 8, 5, 10, 10, 15, 38};
        try {
            pdfTable.setWidths(widths);
        } catch (DocumentException ex) {
            Logger.getLogger(UserDetailsBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        BaseFont helvetica = null;
        try {
            helvetica = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
        } catch (DocumentException | IOException e) {
            Logger.getLogger(UserDetailsBean.class.getName()).log(Level.SEVERE, null, e);
        }
        Font font = new Font(helvetica, 9, Font.BOLD);
        pdfTable.addCell(new Paragraph("Datum", font));
        pdfTable.addCell(new Paragraph("Beginn", font));
        pdfTable.addCell(new Paragraph("Ende", font));
        pdfTable.addCell(new Paragraph("Pause", font));
        pdfTable.addCell(new Paragraph("Sollarbeitszeit", font));
        pdfTable.addCell(new Paragraph("Istarbeitszeit", font));
        pdfTable.addCell(new Paragraph("Überstunden nach 19 Uhr", font));
        pdfTable.addCell(new Paragraph("Bemerkungen", font));

        font = new Font(helvetica, 9, Font.NORMAL);

        for (TimeRowDisplay trd : timerowlist) {
            pdfTable.addCell(new Paragraph(trd.getDate(), font));
            pdfTable.addCell(new Paragraph(trd.getWorkTimeStart(), font));
            pdfTable.addCell(new Paragraph(trd.getWorkTimeEnd(), font));
            if (trd.getBreakTime() == null) {
                pdfTable.addCell(new Paragraph(" ", font));
            } else {
                pdfTable.addCell(new Paragraph(String.valueOf(trd.getBreakTime()), font));
            }
            pdfTable.addCell(new Paragraph(String.valueOf(trd.getSollZeit()).replace("null", ""), font));
            pdfTable.addCell(new Paragraph(String.valueOf(trd.getWorkTime()).replace("null", ""), font));
            pdfTable.addCell(new Paragraph(String.valueOf(trd.getOverTime19plus()).replace("null", ""), font));
            pdfTable.addCell(new Paragraph(trd.getReason(), font));
        }

        font = new Font(helvetica, 9, Font.BOLD);
        pdfTable.addCell(new Paragraph(" ", font));
        pdfTable.addCell(new Paragraph(" ", font));
        pdfTable.addCell(new Paragraph(" ", font));
        pdfTable.addCell(new Paragraph(" ", font));
        pdfTable.addCell(new Paragraph(" ", font));

        DecimalFormat df = new DecimalFormat("#.##");
        pdfTable.addCell(new Paragraph("Saldo: " + df.format(saldo), font));
        pdfTable.addCell(new Paragraph("Gesamt: " + überstundenNach19, font));
        pdfTable.addCell(new Paragraph("Verbleibender Urlaub am Ende des Monats: " + urlaubsanspruch + " Tage", font));

        return pdfTable;
    }

    private void writePDFToResponse(ExternalContext externalContext, ByteArrayOutputStream baos, String fileName) {
        try {
            externalContext.responseReset();
            externalContext.setResponseContentType("application/pdf");
            externalContext.setResponseHeader("Expires", "0");
            externalContext.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            externalContext.setResponseHeader("Pragma", "public");
            externalContext.setResponseHeader("Content-disposition", "attachment;filename=" + fileName + ".pdf");
            externalContext.setResponseContentLength(baos.size());
            OutputStream out = externalContext.getResponseOutputStream();
            baos.writeTo(out);
            externalContext.responseFlushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
