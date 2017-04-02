/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.service.AbsenceService;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

/**
 * A Bean which is used for the Page-"alleAbwesenheiten.xtml"
 *
 * @author msi
 */
public class AlleAbwesenheitenBean {

    List<Absence> absences;

    User currentUser;

    private String selectedUser;
    private List<String> allUsers;

    /**
     * Constructor for AlleAbwesenheitenBean
     */
    public AlleAbwesenheitenBean() {
        absences = AbsenceService.getAllAbsences();

        loadUserSelect();

    }

    /**
     * gets all currently loaded Absences
     *
     * @return List of Absences
     */
    public List<Absence> getAbsences() {
        return absences;
    }

    /**
     * sets all currently loaded Absences
     *
     * @param absences List of Absence to be set
     */
    public void setAbsences(List<Absence> absences) {
        this.absences = absences;
    }

    /**
     * gets the selected User as Username-String
     *
     * @return the selectedUser(name)
     */
    public String getSelectedUser() {
        return selectedUser;
    }

    /**
     * sets the selected User as Username-String
     *
     * @param selectedUser the new selectedUser(name)
     */
    public void setSelectedUser(String selectedUser) {
        this.selectedUser = selectedUser;
    }

    /**
     * get a List with all Users as Usernames
     *
     * @return List of Usernames
     */
    public List<String> getAllUsers() {
        return allUsers;
    }

    /**
     * set a List with all Users as Usernames
     *
     * @param allUsers new List of Usernames
     */
    public void setAllUsers(List<String> allUsers) {
        this.allUsers = allUsers;
    }

    private String loadUserSelect() {
        this.selectedUser = "All";
        this.allUsers = new ArrayList<>();

        allUsers.add("All");

        BenutzerverwaltungService.getUserList().forEach((u) -> {
            allUsers.add(u.getUsername());
        });

        return "/pages/alleAbwesenheiten.xhtml?faces-redirect=true";
    }

    /**
     * loads all Absences based on the selectedUser
     */
    public void loadAllAbsenceByUser() {
        currentUser = BenutzerverwaltungService.getUserByUsername(selectedUser);

        if (selectedUser.equals("All")) {
            absences = AbsenceService.getAllAbsences();
        } else {
            absences = AbsenceService.getAbsenceByUser(currentUser);
            //absence=AbsenceService.getAbsencesByUserBetweenDates(currentUser, new Date(2016, 01, 01, 00, 00, 00), new Date(2016, 12, 31, 00, 00, 00));          
        }
    }

    /**
     * pdf post processing
     *
     * @param document pdf document
     * @throws DocumentException
     */
    public void postProcessPDF(Object document) throws DocumentException {
        Document pdf = (Document) document;

        pdf.add(new Paragraph("\n\nStand: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        pdf.close();
    }

    /**
     * xls post processing
     *
     * @param document xls document
     */
    public void postProcessXLS(Object document) {
        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);

        HSSFRow header = sheet.getRow(0);

        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
            HSSFCell cell = header.getCell(i);
            cell.setCellStyle(cellStyle);

            sheet.autoSizeColumn(i);
        }

        HSSFRow bottomRow = sheet.createRow(sheet.getLastRowNum() + 2);
        bottomRow.createCell(0).setCellValue("Stand: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

    }
}
