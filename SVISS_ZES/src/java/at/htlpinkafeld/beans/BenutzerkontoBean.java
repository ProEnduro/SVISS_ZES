/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.SollZeit;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.WorkTime;
import at.htlpinkafeld.service.AbsenceService;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import at.htlpinkafeld.service.IstZeitService;
import at.htlpinkafeld.service.PasswordEncryptionService;
import at.htlpinkafeld.service.SollZeitenService;
import at.htlpinkafeld.service.TimeConverterService;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import net.fortuna.ical4j.data.ParserException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author ÐarkHell2
 */
@FacesValidator("oldPasswordValidator")
public class BenutzerkontoBean implements Validator {

    private FacesContext context;
    private MasterBean masterBean;

    private ScheduleModel sollzeitModel;
    private User user;
    private LocalDate pointDate;
    private ScheduleEvent curEvent;

    private String newPw = "";
    private String newPw2 = "";

    private UploadedFile excel;

    @PostConstruct
    public void init() {
        pointDate = LocalDate.of(2016, 8, 1);
    }

    public BenutzerkontoBean() {
        context = FacesContext.getCurrentInstance();
        masterBean = (MasterBean) context.getApplication().evaluateExpressionGet(context, "#{masterBean}", MasterBean.class);
        user = masterBean.getUser();
    }

    public void onLoad() {
        user = masterBean.getUser();
        loadSollZeiten();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void saveUser() {
        BenutzerverwaltungService.updateUser(user);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("User gespeichert!"));
    }

    public String getNewPw() {
        return newPw;
    }

    public void setNewPw(String newPw) {
        this.newPw = newPw;
    }

    public String getNewPw2() {
        return newPw2;
    }

    public void setNewPw2(String newPw2) {
        this.newPw2 = newPw2;
    }

    public UploadedFile getExcel() {
        return excel;
    }

    public void setExcel(UploadedFile excel) {
        this.excel = excel;
    }

    public void discardPasswordChanges() {
        newPw = "";
        newPw2 = "";
    }

    public void savePassword() {
        if (newPw.length() < 6) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, "", "Das Passwort muss mindestens 6 Zeichen beinhalten"));
            FacesContext.getCurrentInstance().validationFailed();
        } else if (!newPw.contentEquals(newPw2)) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Die neuen Passwörter stimmen nicht überein"));
            FacesContext.getCurrentInstance().validationFailed();
        } else {
            user.setPass(PasswordEncryptionService.digestPassword(newPw));
            BenutzerverwaltungService.updateUser(user);
        }
    }

    public ScheduleModel getSollzeitModel() {
        return sollzeitModel;
    }

    public void setSollzeitModel(ScheduleModel sollzeitModel) {
        this.sollzeitModel = sollzeitModel;
    }

    public ScheduleEvent getCurEvent() {
        return curEvent;
    }

    public void setCurEvent(ScheduleEvent curEvent) {
        this.curEvent = curEvent;
    }

    public void loadSollZeiten() {
        this.sollzeitModel = new DefaultScheduleModel();

        List<SollZeit> currentSollZeiten;

        currentSollZeiten = SollZeitenService.getSollZeitenByUser(user);

        for (SollZeit sz : currentSollZeiten) {
            if (!sz.getSollStartTime().equals(sz.getSollEndTime())) {
                LocalDate curDate = pointDate.with(TemporalAdjusters.firstInMonth(sz.getDay()));
                DefaultScheduleEvent de = new DefaultScheduleEvent("", TimeConverterService.convertLocalTimeToDate(curDate, sz.getSollStartTime()),
                        TimeConverterService.convertLocalDateTimeToDate(LocalDateTime.of(curDate, sz.getSollEndTime())), curDate.getDayOfWeek());
                sollzeitModel.addEvent(de);
            }
        }
    }

    public void onEventSelect(SelectEvent selectEvent) {
        curEvent = (ScheduleEvent) selectEvent.getObject();
    }

    public Date getPointDate() {
        return IstZeitService.convertLocalDateTimeToDate(pointDate.atStartOfDay());
    }

    @Override
    public void validate(FacesContext fc, UIComponent uic, Object o) throws ValidatorException {
        if (!PasswordEncryptionService.digestPassword(o.toString()).contentEquals(user.getPass())) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Altes Passwort stimmt nicht"));
        }
    }

    public void loadFromExcel(ActionEvent event) throws FileNotFoundException, IOException, ParserException {

        if (excel != null) {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Successful", excel.getFileName() + " successfully uploaded!"));

            XSSFWorkbook workbook = new XSSFWorkbook(excel.getInputstream());

            for (int i = 1; i <= 12; i++) {
                int min = 5;
                LocalDate date = LocalDate.of(2016, i, 1);
                int max = min + date.lengthOfMonth() - 1;

                XSSFSheet sheet = workbook.getSheetAt(i);

                for (int j = min; j <= max; j++) {
                    Row row = sheet.getRow(j);
                    LocalDateTime start = null;
                    LocalDateTime end = null;

                    LocalDate day = date.withDayOfMonth((int) row.getCell(1).getNumericCellValue());

//                DataFormatter formatter = new DataFormatter();
//                System.out.println(formatter.formatCellValue(row.getCell(2)));
                    FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

                    Cell urlaub = row.getCell(10);

                    if (urlaub != null && urlaub.getCellType() != Cell.CELL_TYPE_BLANK && urlaub.getNumericCellValue() != 1.0) {

                        Cell cell = row.getCell(2);

                        //for endtime = row 2
                        if (cell != null) {
                            CellValue cellValue = evaluator.evaluate(cell);
                            if (cellValue != null) {
                                double time = cellValue.getNumberValue() * 24;

                                String time2;
                                DecimalFormat df = new DecimalFormat("00.00");
                                time2 = df.format(time);
                                time2 = time2.replace(',', ':');
                                LocalTime localtime = LocalTime.parse(time2);
                                start = LocalDateTime.of(day, localtime);
                            }
                        }
                        cell = row.getCell(3);

                        //for endtime = row 3
                        if (cell != null) {
                            CellValue cellValue = evaluator.evaluate(cell);
                            if (cellValue != null) {
                                double time = cellValue.getNumberValue() * 24;

                                String time2;
                                DecimalFormat df = new DecimalFormat("00.00");
                                time2 = df.format(time);
                                time2 = time2.replace(',', ':');
                                LocalTime localtime = LocalTime.parse(time2);
                                end = LocalDateTime.of(day, localtime);
                            }
                        }

                        int breaktime = 0;
                        cell = row.getCell(4);
                        if (cell != null) {
                            CellValue cellValue = evaluator.evaluate(cell);
                            if (cellValue != null) {
                                double tempbreaktime = cellValue.getNumberValue() * 24 * 60;
                                breaktime = (int) tempbreaktime;
                            }
                        }

                        String bemerkung = "";
                        Cell comment = row.getCell(11);
                        if (comment != null) {
                            CellValue value = evaluator.evaluate(comment);

                            if (value != null) {
                                bemerkung = value.formatAsString();
                                double d;
                                try {
                                    d = Double.valueOf(bemerkung);
                                    if (BigDecimal.valueOf(d).scale() > 2) {
                                        d = d * 24 * 60;
                                        LocalTime lt = LocalTime.MIN.plusMinutes((int) (d + 0.5));
                                        bemerkung = lt.format(DateTimeFormatter.ofPattern("HH:mm"));
                                    }
                                } catch (Exception e) {
                                    //Value is not castable to double and will be ignored -> best case scenario
                                }
                            }
                        }

                        if (start != null && end != null) {
                            WorkTime worktime = new WorkTime(user, start, end, breaktime, bemerkung, "");
                            IstZeitService.addIstTime(worktime);
                        }
                    } else if (urlaub.getCellType() != Cell.CELL_TYPE_BLANK && urlaub.getNumericCellValue() == 1.0) {

                        start = LocalDateTime.of(day, LocalTime.MIN);
                        end = start;

                        Absence a = new Absence(user, AbsenceService.getAbsenceTypeByID(2), start, end);
                        AbsenceService.insertAbsence(a);
                    }
                }
            }
        }
    }

}
