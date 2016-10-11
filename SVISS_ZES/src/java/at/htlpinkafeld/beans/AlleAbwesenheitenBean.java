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
 *
 * @author msi
 */
public class AlleAbwesenheitenBean {

    List<Absence> absence;

    User currentUser;

    private String selectedUser;
    private List<String> allUsers;

    public AlleAbwesenheitenBean() {
        absence = AbsenceService.getAllAbsences();

        this.loadUserSelect();

    }

    public List<Absence> getAbsence() {
        return absence;
    }

    public void setAbsence(List<Absence> absence) {
        this.absence = absence;
    }

    public String getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(String selectedUser) {
        this.selectedUser = selectedUser;
    }

    public List<String> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(List<String> allUsers) {
        this.allUsers = allUsers;
    }

    public String loadUserSelect() {
        this.selectedUser = "All";
        this.allUsers = new ArrayList<>();

        allUsers.add("All");

        for (User u : BenutzerverwaltungService.getUserList()) {
            allUsers.add(u.getUsername());
        }

        return "/pages/alleAbwesenheiten.xhtml?faces-redirect=true";
    }

    public void loadAllAbsenceByUser() {
        currentUser = BenutzerverwaltungService.getUserByUsername(selectedUser);

        if (selectedUser.equals("All")) {
            absence = AbsenceService.getAllAbsences();
        } else {
            absence = AbsenceService.getAbsenceByUser(currentUser);
            //absence=AbsenceService.getAbsencesByUserBetweenDates(currentUser, new Date(2016, 01, 01, 00, 00, 00), new Date(2016, 12, 31, 00, 00, 00));          
        }
    }

    public void postProcessPDF(Object document) throws DocumentException {
        Document pdf = (Document) document;

        pdf.add(new Paragraph("\n\nStand: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        pdf.close();
    }
    
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
