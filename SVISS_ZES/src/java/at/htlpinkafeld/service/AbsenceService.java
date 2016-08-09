/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.User;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author msi
 */
public class AbsenceService {
    
    public static List<Absence> getAbsenceByUserAndUnacknowledged(User u){
        List<Absence> absenceList;
        
        absenceList = DAOFactory.getDAOFactory().getAbsenceDAO().getAbsencesByUserAndAcknowledgment(u, false);
        
        return absenceList;
    }
    
    public static List<Absence> getAbsenceByUser(User u){
        List<Absence> absenceList;
        
        absenceList = DAOFactory.getDAOFactory().getAbsenceDAO().getAbsencesByUser(u);
        
        return absenceList;
    }
    
    public static void updateAbsence(Absence a){
        DAOFactory.getDAOFactory().getAbsenceDAO().update(a);
    }
    
    public static List<Absence> getAllUnacknowledged(){
        return DAOFactory.getDAOFactory().getAbsenceDAO().getAbsencesByAcknowledgment(false);
    }
    
    public static List<Absence> getAllAcknowledged(){
        return DAOFactory.getDAOFactory().getAbsenceDAO().getAbsencesByAcknowledgment(true);
    }

    public static List<Absence> getAbsenceByUserAndAcknowledged(User u) {
        return DAOFactory.getDAOFactory().getAbsenceDAO().getAbsencesByUserAndAcknowledgment(u, true);
    }
    
    public static void removeAbsence(Absence a){
        DAOFactory.getDAOFactory().getAbsenceDAO().delete(a);
    }
}
