/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.User;
import java.util.List;
import javax.faces.context.FacesContext;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.chart.PieChartModel;

/**
 * Bean which is used to redirect to the "auswertungspages"
 *
 * @author msi
 */
public class AuswertungsBean {

    /**
     * Creates a new instance of AuswertungsBean
     */
    String image;
    StreamedContent file;

    User currentUser;
    String selectedUser;
    List<String> allUser;

    PieChartModel pieModel1;

    public AuswertungsBean() {

    }

    /**
     * reloads the current User
     */
    public void reloadCurrentUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        ScheduleView scheduleView = (ScheduleView) context.getApplication().evaluateExpressionGet(context, "#{scheduleView}", ScheduleView.class);
        currentUser = scheduleView.getCurrentUser();
    }

    /**
     * does something
     *
     * @return new URL
     */
    public String auswerten() {
        reloadCurrentUser();
//        this.createPieModels();

        return "/pages/auswertungspages/jahresuebersicht.xhtml?faces-redirect=true";
    }


//    public boolean isNotUser() {
//        if (currentUser.getAccessLevel().getAccessLevelID() == 4) {
//            return false;
//        } else {
//
//            selectedUser = "All";
//            allUser = new ArrayList<>();
//            allUser.add("All");
//
//            BenutzerverwaltungService.getUserList().forEach((u) -> {
//                allUser.add(u.getUsername());
//            });
//            return true;
//        }
//    }

//    public String getSelectedUser() {
//        return selectedUser;
//    }
//
//    public void setSelectedUser(String selectedUser) {
//        this.selectedUser = selectedUser;
//    }
//
//    public List<String> getAllUser() {
//        return allUser;
//    }
//
//    public void setAllUser(List<String> allUser) {
//        this.allUser = allUser;
//    }
//
//    public PieChartModel getPieModel1() {
//        return pieModel1;
//    }
//
//    public void setPieModel1(PieChartModel pieModel1) {
//        this.pieModel1 = pieModel1;
//    }

//    private void createPieModels() {
//        createPieModel1();
//    }

//    private void createPieModel1() {
//        pieModel1 = new PieChartModel();
//        double istzeit = 0;
//        double holiday = 0;
//        double medical = 0;
//        double timecompensation = 0;
//        double business = 0;
//
//        List<WorkTime> workList;
//        List<Absence> absenceList;
//
//        if (null == selectedUser) {
//            if (this.isNotUser()) {
//                workList = IstZeitService.getAllWorkTime();
//                absenceList = AbsenceService.getAllAcknowledged();
//            } else {
//                workList = IstZeitService.getWorktimeByUser(currentUser);
//                absenceList = AbsenceService.getAbsenceByUserAndAcknowledged(currentUser);
//            }
//        } else {
//            switch (selectedUser) {
//                case "All":
//                    workList = IstZeitService.getAllWorkTime();
//                    absenceList = AbsenceService.getAllAcknowledged();
//                    break;
//                default:
//                    workList = IstZeitService.getWorktimeByUser(BenutzerverwaltungService.getUser(selectedUser));
//                    absenceList = AbsenceService.getAbsenceByUserAndAcknowledged(BenutzerverwaltungService.getUser(selectedUser));
//                    break;
//            }
//        }
//
//        for (WorkTime w : workList) {
//            double t = w.getStartTime().until((w.getEndTime()), ChronoUnit.MINUTES) / 60.0 - w.getBreakTime() / 60.0;
//
//            istzeit = istzeit + t;
//        }
//        pieModel1.set("IstZeit", istzeit);
//
//        for (Absence a : absenceList) {
//            double t = a.getStartTime().until((a.getEndTime()), ChronoUnit.MINUTES) / 60.0;
//            switch (a.getAbsenceType()) {
//                case MEDICAL_LEAVE:
//                    medical = medical + t;
//                    break;
//                case HOLIDAY:
//                    holiday = holiday + t;
//                    break;
//                case TIME_COMPENSATION:
//                    timecompensation = timecompensation + t;
//                    break;
//                case BUSINESSRELATED_ABSENCE:
//                    business = business + t;
//                    break;
//            }
//        }
//        pieModel1.set("medical", medical);
//        pieModel1.set("holiday", holiday);
//        pieModel1.set("timecompensation", timecompensation);
//        pieModel1.set("business", business);
//
//        pieModel1.setTitle("Auswertung aller Zeiten in Stunden");
//        pieModel1.setLegendPosition("e");
//
////        pieModel1.setExtender("pieExtender");
////        pieModel1.setShowDataLabels(true);
//        selectedUser = null;
//    }

//    public String getImage() {
//        return image;
//    }
//
//    public void setImage(String image) {
//        this.image = image;
//    }
//
//    public void renderPicture(ActionEvent e) {
//        if (this.image.split(",").length > 1) {
//            String encoded = image.split(",")[1];
//            byte[] decoded = Base64.decodeBase64(encoded);
//
//            try {
//                RenderedImage renderedImage = ImageIO.read(new ByteArrayInputStream(decoded));
//
//                ServletContext serv = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
//                String path = serv.getRealPath("/");
//                boolean b = ImageIO.write(renderedImage, "png", new File(path + "/pdfs/out.png"));
//
//                if (b) {
//
//                    testPDF(null);
//                    InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/pdfs/test.pdf");
//                    file = new DefaultStreamedContent(stream, "application/pdf", "auswertung_" + this.selectedUser + ".pdf");
//                }
//            } catch (IOException ex) {
//                Logger.getLogger(AuswertungsBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//        }
//    }

//    public void testPDF(ActionEvent e) throws IOException {
//
//        ServletContext serv = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
//        String path = serv.getRealPath("/") + "/pdfs/";
//
//        BufferedImage img = ImageIO.read(new File(path + "out.png"));
//
//        try (//        function for joining two pictures -> 
//                //        BufferedImage joined = joinBufferedImage(one, two);
//                //        boolean success = ImageIO.write(joined, "png", new File(path + "joined.png"));
//                //        System.out.println("saved success? " + success);
//                PDDocument doc = new PDDocument()) {
//            PDPage blank = new PDPage();
//            doc.addPage(blank);
//            PDImageXObject imageXObject = JPEGFactory.createFromImage(doc, img);
//            try (PDPageContentStream pageContentStream = new PDPageContentStream(doc, blank, true, true, true)) {
//                int w = (int) (img.getWidth() / 3);
//                int h = (int) (img.getHeight() / 3);
//
//                pageContentStream.drawXObject(imageXObject, 0, 792 - h, w, h);
//            }
//            doc.save(new File(path + "test.pdf"));
//        }
//    }

//    public static BufferedImage joinBufferedImage(BufferedImage img1, BufferedImage img2) {
//
//        //do some calculate first
//        int offset = 5;
//        int wid = img1.getWidth() + img2.getWidth() + offset;
//        int height = Math.max(img1.getHeight(), img2.getHeight());
//        //create a new buffer and draw two image into the new image
//        BufferedImage newImage = new BufferedImage(wid, height, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g2 = newImage.createGraphics();
//        Color oldColor = g2.getColor();
//        //fill background
//        g2.setPaint(Color.WHITE);
//        g2.fillRect(0, 0, wid, height);
//        //draw image
//        g2.setColor(oldColor);
//        g2.drawImage(img1, null, 0, 0);
//        g2.drawImage(img2, null, img1.getWidth() + offset, 0);
//        g2.dispose();
//        return newImage;
//    }

//    public StreamedContent getFile() {
//        return file;
//    }
//
//    public void setFile(StreamedContent file) {
//        this.file = file;
//    }

    public String redirectToOverview() {
        return "/pages/auswertungspages/overview.xhtml?faces-redirect=true";
    }

    public String redirectToAlleAbwesenheiten() {
        return "/pages/auswertungspages/alleAbwesenheiten.xhtml?faces-redirect=true";
    }

    public String redirectToJahresuebersicht() {
        return "/pages/auswertungspages/jahresuebersicht.xhtml?faces-redirect=true";
    }

    public String redirectToUserDetails() {
        return "/pages/auswertungspages/userDetails.xhtml?faces-redirect=true";
    }

}
