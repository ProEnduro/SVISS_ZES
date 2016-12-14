/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.Base_DAO;
import at.htlpinkafeld.mobileInterface.authorization.Secured;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Martin Six
 */
@javax.ejb.Stateless
@Path("absence")
public class AbsenceFacadeREST extends AbstractFacade<Absence> {
    
    @PersistenceContext(unitName = "SVISS_ZESPU")
    private Base_DAO dao;
    private ObjectMapper mapper;
    
    public AbsenceFacadeREST() {
        super(Absence.class);
        dao = DAOFactory.getDAOFactory().getAbsenceDAO();
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        
    }
    
    @POST
    @Secured
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Absence create(String entity) {
        Absence a = null;
        try {
            a = mapper.readValue(entity, Absence.class);
        } catch (IOException ex) {
            Logger.getLogger(UserFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return super.create(a);
    }
    
    @PUT
    @Secured
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(String entity) {
        Absence a = null;
        try {
            a = mapper.readValue(entity, Absence.class);
        } catch (IOException ex) {
            Logger.getLogger(UserFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.edit(a);
    }
    
    @DELETE
    @Secured
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void remove(String entity) {
        Absence a = null;
        try {
            a = mapper.readValue(entity, Absence.class);
        } catch (IOException ex) {
            Logger.getLogger(UserFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.remove(a);
    }
    
    @GET
    @Secured
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Absence> findAll() {
        List<Absence> absences = super.findAll();
        for (Absence a : absences) {
            a.setUser(new UserImpl(a.getUser()));
        }
        return absences;
    }
    
    @Override
    protected Base_DAO getDAO() {
        return dao;
    }
    
}
