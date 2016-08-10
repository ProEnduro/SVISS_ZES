/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.WorkTime;
import at.htlpinkafeld.service.AbsenceService;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import at.htlpinkafeld.service.IstZeitService;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.imageio.ImageIO;
import static javax.imageio.ImageIO.write;
import javax.servlet.ServletContext;
import org.apache.tomcat.util.codec.binary.Base64;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author msi
 */
public class AuswertungsBean {

    /**
     * Creates a new instance of AuswertungsBean
     */
    String image;

    User currentUser;
    String selectedUser;
    List<String> allUser;

    PieChartModel pieModel1;

    public AuswertungsBean() {

    }

    public void reloadCurrentUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        ScheduleView scheduleView = (ScheduleView) context.getApplication().evaluateExpressionGet(context, "#{scheduleView}", ScheduleView.class);
        currentUser = scheduleView.getCurrentUser();
    }

    public void auswerten(ActionEvent e) {
        reloadCurrentUser();
        this.createPieModels();
    }

    public boolean isNotUser() {
        if (currentUser.getAccessLevel().getAccessLevelID() == 4) {
            return false;
        } else {

            selectedUser = "All";
            allUser = new ArrayList<>();
            allUser.add("All");

            for (User u : BenutzerverwaltungService.getUserList()) {
                allUser.add(u.getUsername());
            }
            return true;
        }
    }

    public String getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(String selectedUser) {
        this.selectedUser = selectedUser;
    }

    public List<String> getAllUser() {
        return allUser;
    }

    public void setAllUser(List<String> allUser) {
        this.allUser = allUser;
    }

    public PieChartModel getPieModel1() {
        return pieModel1;
    }

    public void setPieModel1(PieChartModel pieModel1) {
        this.pieModel1 = pieModel1;
    }

    private void createPieModels() {
        createPieModel1();
    }

    private void createPieModel1() {
        pieModel1 = new PieChartModel();
        double istzeit = 0;
        double holiday = 0;
        double medical = 0;
        double timecompensation = 0;
        double business = 0;

        List<WorkTime> workList;
        List<Absence> absenceList;

        if (selectedUser == null) {
            if (this.isNotUser()) {
                workList = IstZeitService.getAllWorkTime();
                absenceList = AbsenceService.getAllAcknowledged();
            } else {
                workList = IstZeitService.getWorktimeByUser(currentUser);
                absenceList = AbsenceService.getAbsenceByUserAndAcknowledged(currentUser);
            }
        } else {
            if (selectedUser.equals("All")) {
                workList = IstZeitService.getAllWorkTime();
                absenceList = AbsenceService.getAllAcknowledged();
            } else {
                workList = IstZeitService.getWorktimeByUser(BenutzerverwaltungService.getUser(selectedUser));
                absenceList = AbsenceService.getAbsenceByUserAndAcknowledged(BenutzerverwaltungService.getUser(selectedUser));
            }
        }

        for (WorkTime w : workList) {
            double t = w.getStartTime().until((w.getEndTime()), ChronoUnit.MINUTES) / 60.0 - w.getBreakTime() / 60.0;

            istzeit = istzeit + t;
        }
        pieModel1.set("IstZeit", istzeit);

        for (Absence a : absenceList) {
            double t = a.getStartTime().until((a.getEndTime()), ChronoUnit.MINUTES) / 60.0;
            switch (a.getAbsenceType().getAbsenceTypeID()) {
                case 1:
                    medical = medical + t;
                    break;
                case 2:
                    holiday = holiday + t;
                    break;
                case 3:
                    timecompensation = timecompensation + t;
                    break;
                case 4:
                    business = business + t;
                    break;
            }
        }
        pieModel1.set("medical", medical);
        pieModel1.set("holiday", holiday);
        pieModel1.set("timecompensation", timecompensation);
        pieModel1.set("business", business);

        pieModel1.setTitle("Auswertung aller Zeiten in Stunden");
        pieModel1.setLegendPosition("e");
        pieModel1.setExtender("pieExtender");
        pieModel1.setShowDataLabels(true);

        selectedUser = null;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void renderPicture(ActionEvent e) {
        if (this.image.split(",").length > 1) {
            String encoded = image.split(",")[1];
            byte[] decoded = Base64.decodeBase64(encoded);

            try {
                RenderedImage renderedImage = ImageIO.read(new ByteArrayInputStream(decoded));

                ServletContext serv = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
                String path = serv.getRealPath("/");
                ImageIO.write(renderedImage, "png", new File(path + "/out.png"));
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
